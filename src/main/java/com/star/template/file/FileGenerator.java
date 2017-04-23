package com.star.template.file;

import com.star.collection.ArrayUtil;
import com.star.exception.pojo.ToolException;
import com.star.io.file.FileNameUtils;
import com.star.io.file.PathUtil;
import com.star.string.StringUtil;
import com.star.template.db.model.Table;
import com.star.template.db.table.Factory;
import com.star.template.db.util.PropertyUtil;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件生成器
 * <p>
 * Created by win7 on 2017/3/18.
 */
public final class FileGenerator {

    private static final String PACK = "basepackage";
    private static final String CLASSNAME = "className";
    private static final String EXT = "include";


    private FileGenerator() {
    }

    public static void generateByNames(final String[] tableNames) {
        String[] names = tableNames.clone();
        List<Table> tables;
        if (ArrayUtil.isEmpty(names)) {
            tables = Factory.getInstance().getAllTables();
        } else if (tableNames.length > 1) {
            tables = Factory.getInstance().getTableByNames(names);
        } else {
            Table table = Factory.getInstance().getTableByName(names[0]);
            tables = Arrays.asList(table);
        }

        final Path src = Paths.get(PropertyUtil.getTemplatePath());
        final Path dest = Paths.get(PropertyUtil.getOutPath());
        final String packageString = PropertyUtil.getPackage();

        final String dirString = packageString.replace(StringUtil.DOT, StringUtil.BACKSLASH);

        final Map<String, Object> maps = new HashMap<>(2);
        maps.put(PACK, dirString);

        FileResourceLoader loader = new FileResourceLoader(PropertyUtil.getTemplatePath());


        try {
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate groupTemplate = new GroupTemplate(loader, cfg);
            Map<String, Object> shared = new HashMap<>();
            shared.put(PACK, packageString);
            groupTemplate.setSharedVars(shared);
            for (Table table : tables) {
                Files.walkFileTree(src, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        final Path targetPath = dest.resolve(src.relativize(dir));
                        String pathString = targetPath.toString();
                        if (pathString.contains(PACK)) {
                            pathString = StringUtil.format(pathString, maps);
                        }

                        PathUtil.mkDirs(Paths.get(pathString));
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (!EXT.equalsIgnoreCase(FileNameUtils.extName(file.toString()))) {
                            Path path = dest.resolve(src.relativize(file));
                            String pathString = path.toString();
                            if (pathString.contains(CLASSNAME)) {
                                maps.put(CLASSNAME, table.getClassName());
                                pathString = StringUtil.format(pathString, maps);
                                Template template = groupTemplate.getTemplate(src.relativize(file).toString());
                                template.binding("table", table);

                                path = Paths.get(pathString);
                                template.renderTo(Files.newBufferedWriter(path));
                                template.renderTo(Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING));

                            } else {
                                if (pathString.contains(PACK)) {
                                    pathString = StringUtil.format(pathString, maps);
                                    path = Paths.get(pathString);
                                }
                                Files.copy(file, path, StandardCopyOption.REPLACE_EXISTING);
                            }

                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (IOException e) {
            throw new ToolException(StringUtil.format("generate code file failure: {}", e.getMessage()), e);
        }

    }

}
