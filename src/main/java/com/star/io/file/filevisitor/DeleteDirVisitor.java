package com.star.io.file.filevisitor;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 删除文件夹及下面内容
 * 
 * @author starhq
 *
 */
public class DeleteDirVisitor extends SimpleFileVisitor<Path> {

	/**
	 * 删除文件
	 */
	@Override
	public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
		Files.deleteIfExists(file);
		return FileVisitResult.CONTINUE;
	}

	/**
	 * 删除目录
	 */
	@Override
	public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
		if (exc == null) {
			Files.deleteIfExists(dir);
			return FileVisitResult.CONTINUE;
		}
		throw exc;
	}

}
