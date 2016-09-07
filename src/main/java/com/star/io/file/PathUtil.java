package com.star.io.file;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import com.star.clazz.ClassUtil;
import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 路径工具类
 * 
 * @author starhq
 *
 */
public final class PathUtil {

	private PathUtil() {
	}

	/**
	 * 判断目录是否为空
	 */
	public static boolean isEmpty(final Path dir) {
		Assert.isTrue(exist(dir), "determine whether the directory is empty failure,the directory not exists");
		try {
			final DirectoryStream<Path> dirs = Files.newDirectoryStream(dir);
			return !dirs.iterator().hasNext();
		} catch (IOException e) {
			throw new ToolException(StringUtil
					.format("determine whether the directory is empty failure,the reasone is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 判断path是否存在
	 */
	public static boolean exist(final Path path) {
		return Objects.isNull(path) ? false : Files.exists(path);
	}

	/**
	 * 创建文件
	 */
	public static Path touch(final Path path) {
		if (!Files.exists(path)) {
			mkParentDirs(path);
			try {
				Files.createFile(path);
			} catch (IOException e) {
				throw new ToolException(
						StringUtil.format("touch path {} failure.the reason is: {}", path, e.getMessage()), e);
			}
		}
		return path;
	}

	/**
	 * 创建父目录
	 */
	public static Path mkParentDirs(final Path path) {
		final Path parent = path.getParent();
		if (!Files.exists(parent)) {
			try {
				Files.createDirectories(parent);
			} catch (IOException e) {
				throw new ToolException(
						StringUtil.format("make {}'s parent dir failure,the reason is: {}", path, e.getMessage()), e);
			}
		}
		return parent;
	}

	/**
	 * 创建dir
	 */
	public static Path mkDirs(final Path path) {
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				throw new ToolException(
						StringUtil.format("make {}'s  dir failure,the reason is: {}", path, e.getMessage()), e);
			}
		}
		return path;
	}

	/**
	 * 获得绝对路径
	 */
	public static Path getAbsolutePath(final String path) {
		Assert.notNull(path, "get absolute path failure,input path is null");
		final Path result = Paths.get(path);
		return result.isAbsolute() ? result : result.toAbsolutePath();
	}

	/**
	 * 获得绝对路径
	 */
	public static Path getAbsoluteClassPath(final String path) {
		Assert.notNull(path, "get absolute class path failure,input path is null");
		final URL url = ClassUtil.getURL(path);
		try {
			final Path result = Paths.get(url.toURI());
			return result.isAbsolute() ? result : result.toAbsolutePath();
		} catch (URISyntaxException e) {
			throw new ToolException(
					StringUtil.format("get {}'s absolure class path failure,the reason is {}", path, e.getMessage()),
					e);
		}
	}

	/**
	 * 相对子路径
	 * 
	 * 简单点就是child路径去掉root路径
	 */
	public static Path subPath(final Path root, final Path child) {
		Assert.isTrue(child.startsWith(root), StringUtil.format("Path {} is not Path {}'s parent", root, child));
		return child.subpath(root.getNameCount(), child.getNameCount());
	}

}
