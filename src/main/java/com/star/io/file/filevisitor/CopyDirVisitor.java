package com.star.io.file.filevisitor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import com.star.io.file.PathUtil;

/**
 * 拷贝文件
 * 
 * @author starhq
 *
 */
public class CopyDirVisitor extends SimpleFileVisitor<Path> {

	/**
	 * 目标
	 */
	private transient final Path src;
	/**
	 * 目的地
	 */
	private transient final Path dest;
	/**
	 * 拷贝选项
	 */
	private transient StandardCopyOption copyOption;

	/**
	 * 构造方法
	 */
	public CopyDirVisitor(final Path src, final Path dest, final StandardCopyOption copyOption) {
		super();
		this.src = src;
		this.dest = dest;
		this.copyOption = copyOption;
	}

	/**
	 * 构造方法
	 */
	public CopyDirVisitor(final Path src, final Path dest) {
		this(src, dest, StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * 目标文件不存在则创建
	 */
	@Override
	public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {

		final Path targetPath = dest.resolve(src.relativize(dir));
		PathUtil.mkDirs(targetPath);
		return FileVisitResult.CONTINUE;
	}

	/**
	 * 拷贝文件,默认是存在就替换
	 */
	@Override
	public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {

		Files.copy(file, dest.resolve(src.relativize(file)), copyOption);
		return FileVisitResult.CONTINUE;
	}

}
