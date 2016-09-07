/**
 * 
 */
package com.star.io.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.star.exception.pojo.ToolException;
import com.star.io.file.filevisitor.CopyDirVisitor;
import com.star.io.file.filevisitor.DeleteDirVisitor;
import com.star.io.file.filevisitor.MoveDirVisitor;
import com.star.lang.Assert;
import com.star.lang.Filter;
import com.star.string.StringUtil;

/**
 * 文件操作工具类
 * 
 * @author starhq
 *
 */
public final class FileUtil {

	/**
	 * 
	 */
	private FileUtil() {
	}

	/**
	 * 遍历目录，获得所有文件
	 */
	public static List<Path> loopFiles(final Path path, final Filter<Path> fileFilter,
			final Filter<Path> directoryFilter) {
		Assert.isTrue(PathUtil.exist(path),
				StringUtil.format("get path {}'s file failure,input path is not exists", path));
		Assert.isTrue(Files.isDirectory(path),
				StringUtil.format("get path {}'s file failure,input path is not directory", path));
		final List<Path> paths = new ArrayList<>();
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

				/**
				 * 遍历目录
				 */
				@Override
				public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
						throws IOException {
					return Objects.isNull(directoryFilter) ? FileVisitResult.CONTINUE
							: directoryFilter.accept(dir) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
				}

				/**
				 * 遍历文件
				 */
				@Override
				public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
					if (Objects.isNull(fileFilter) || !Objects.isNull(fileFilter) && fileFilter.accept(file)) {
						paths.add(file);
					}

					return FileVisitResult.CONTINUE;
				}

			});
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("get path {}'s file failure,the reason is {}", path, e.getMessage()), e);
		}

		return paths;
	}

	/**
	 * 递归删除文件
	 * 
	 */
	public static void loopDelete(final Path path) {
		Assert.isTrue(PathUtil.exist(path),
				StringUtil.format("delete path {}'s file failure,input path is not exists", path));
		Assert.isTrue(Files.isDirectory(path),
				StringUtil.format("delete path {}'s file failure,input path is not directory", path));
		try {
			Files.walkFileTree(path, new DeleteDirVisitor());
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("loop delete direcotry {}'s file failure,the reason is {}", path, e.getMessage()),
					e);
		}
	}

	/**
	 * 复制文件,成功后返回dest
	 * 
	 * 会有递归请在调用之前保证file的合法性(src存在，src和dest不相等，src是目录的时候，dest不能是文件)
	 * 
	 * 这个快
	 * 
	 */
	// public static File copy(final File src, final File dest) {
	// File result = null;
	// if (src.isDirectory()) {
	// if (!dest.exists()) {
	// dest.mkdirs();
	// }
	// final File[] files = src.listFiles();
	// if (files != null && files.length > 0) {
	// for (final File file : files) {
	// copy(file, new File(dest, file.getName()));
	// }
	// }
	// result = dest;
	// }
	//
	// if (result == null) {
	// FileInputStream input = null;
	// FileOutputStream output = null;
	//
	// try {
	// input = new FileInputStream(src);
	// output = new FileOutputStream(dest);
	// if (src.length() != IoUtil.copy(input, output, 0)) {
	// throw new ToolException("拷贝失败,拷贝的长度和源文件长度不一致");
	// }
	// result = dest;
	// } catch (FileNotFoundException e) {
	// throw new ToolException(StringUtil.format("拷贝文件失败: {}", e.getMessage()),
	// e);
	// } finally {
	// IoUtil.close(input);
	// IoUtil.close(output);
	// }
	// }
	// return result;
	// }

	/**
	 * nio复制文件
	 * 
	 * 我比较喜欢这中，代码看上去很优雅，不需要递归，但好像没普通的io复制快
	 * 
	 * 文件越大越慢 (src存在，src和dest不相等，src是目录的时候，dest不能是文件)
	 */
	public static void copy(final Path src, final Path dest) {
		Assert.isTrue(PathUtil.exist(src), "src must exists");
		Assert.isTrue(!src.equals(dest), "src and dest path must be  different");
		// 去掉这约束，因为dest有可能没建立
		// dest的建立放到了CopyDirVisitor中
		// Assert.isTrue(Files.isDirectory(src) && Files.isDirectory(dest),
		// "when src is directory,the dest must be directory too");
		try {
			Files.walkFileTree(src, new CopyDirVisitor(src, dest));
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("copy {} to {} failure the reason is {}", src, dest, e.getMessage()), e);
		}
	}

	/**
	 * 移动目录
	 */
	public static void move(final Path src, final Path dest) {
		Assert.isTrue(PathUtil.exist(src), "src must exists");
		Assert.isTrue(!src.equals(dest), "src and dest path must be  different");
		try {
			Files.walkFileTree(src, new MoveDirVisitor(src, dest));
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("move {} to {} failure the reason is {}", src, dest, e.getMessage()), e);
		}
	}

	/**
	 * 移动文件或目录
	 */
	// public static void move(final File src, final File dest, final boolean
	// isOverride) {
	// if (src == null || !src.exists()) {
	// throw new ToolException("移动文件失败,输入的参数为空或者不存在");
	// }
	// if (dest.exists() && isOverride) {
	// dest.delete();
	// }
	// if (src.isDirectory() && dest.isFile()) {
	// throw new ToolException("移动文件失败,源为目录而目的却是文件");
	// }
	// if (!src.renameTo(dest)) {
	// try {
	// // copy(src, dest);
	// } catch (ToolException e) {
	// throw new ToolException(StringUtil.format("移动文件失败: {}", e.getMessage()),
	// e);
	// }
	// // delFile(src);
	// }
	// }

	/**
	 * 获取文件的行数，配合readline做进一步处理 这个还没想好派什么用处
	 */
	// public static int countLines(final Reader reader) {
	// final char[] character = new char[1024];
	// int count = 0;
	// int readChars = 0;
	// boolean empty = true;
	// try {
	// while ((readChars = reader.read(character)) != -1) {
	// empty = false;
	// for (int i = 0; i < readChars; ++i) {
	// if (character[i] == '\n') {
	// ++count;
	// }
	// }
	// }
	// } catch (IOException e) {
	// throw new ToolException(StringUtil.format("获取文件行数失败: {}",
	// e.getMessage()), e);
	// }
	// return count == 0 && !empty ? 1 : count;
	// }

}
