package com.star.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.star.collection.CollectionUtil;
import com.star.exception.pojo.ToolException;
import com.star.io.file.FileIoUtil;
import com.star.regex.RegexUtil;
import com.star.string.StringUtil;

/**
 * dom4j操作xml
 * 
 * @author starhq
 *
 */
public final class Dom4JUtil {

	private Dom4JUtil() {
	}

	/**
	 * 
	 * @param xmlSource
	 * @param elementName
	 * @return
	 */
	public static String getElementTitle(final String xmlSource, final String elementName) {
		try {
			final SAXReader saxReader = new SAXReader();
			final Document document = saxReader.read(new ByteArrayInputStream(xmlSource.getBytes()));
			final Element root = document.getRootElement(); // 获得根节点

			// 得到database节点

			final Element database = (Element) root.selectSingleNode(elementName);
			return Objects.isNull(database) ? StringUtil.EMPTY : database.getText();
		} catch (DocumentException e) {
			throw new ToolException(StringUtil.format("get element title failure,the reason is: {}", e.getMessage()),
					e);
		}
	}

	/**
	 * 查询元素节点
	 */
	public boolean selectElement(final String xmlSource, final String elementName) {
		boolean result = false;
		if (!StringUtil.isBlank(xmlSource)) {
			try {
				final String xml = xmlSource.replaceAll(">>", ">");
				final SAXReader saxReader = new SAXReader();
				final Document document = saxReader.read(new ByteArrayInputStream(xml.getBytes()));
				final Element root = document.getRootElement(); // 获得根节点

				// 得到database节点

				final Element database = (Element) root.selectSingleNode(elementName);
				result ^= Objects.isNull(database);
			} catch (DocumentException e) {
				throw new ToolException(StringUtil.format("select element failure,the reason is: {}", e.getMessage()),
						e);
			}
		}
		return result;
	}

	/**
	 * 
	 * 按节点名，取节点属性值
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static String getElementProperty(final String xmlSource, final String elementName,
			final String propertyName) {
		try {
			final SAXReader saxReader = new SAXReader();
			final Document document = saxReader.read(new ByteArrayInputStream(xmlSource.getBytes()));
			final Element root = document.getRootElement(); // 获得根节点

			final List<Element> list = (List<Element>) root.selectNodes(elementName);
			return list.isEmpty() ? StringUtil.EMPTY : list.get(0).attributeValue(propertyName);
		} catch (DocumentException e) {
			throw new ToolException(
					StringUtil.format("get element's attribute failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 更新属性
	 */
	@SuppressWarnings("unchecked")
	public static String updataElementProperty(final String xmlSource, final String elementName,
			final String propertyName, final String propertyValue) {
		try {
			final SAXReader saxReader = new SAXReader();
			final Document document = saxReader.read(new ByteArrayInputStream(xmlSource.getBytes()));
			final Element root = document.getRootElement(); // 获得根节点

			// 得到database节点

			final List<Element> list = (List<Element>) root.selectNodes(elementName);

			for (final Element element : list) {
				final Attribute attribute = element.attribute(propertyName);
				if (!Objects.isNull(attribute)) {
					attribute.setValue(propertyValue);
				}
			}
			return document.asXML();
		} catch (DocumentException e) {
			throw new ToolException(
					StringUtil.format("update element's attribute failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 通过正则表达式,按元素分割
	 */
	public static List<String> partitionXml(final String sourceXml, final String elementName) {
		final String template = StringUtil.format("<{}(.*?\\n?)*?</{}>", elementName, elementName);
		final List<String> strings = CollectionUtil.getList(null);
		RegexUtil.findAll(Pattern.compile(template), sourceXml, 0, strings);
		return strings;
	}

	/**
	 * 判断是否只有一个根节点
	 */
	public boolean needConvert(final String xmlSource) {
		try {
			final SAXReader saxReader = new SAXReader();
			final Document document = saxReader.read(new ByteArrayInputStream(xmlSource.getBytes()));
			final Element element = document.getRootElement();
			return element.elements().size() > 0;
		} catch (DocumentException e) {
			throw new ToolException(StringUtil.format(
					"determine whether xml only has one root element failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * xml写入文件
	 */
	public static void xmlWriteToFile(final Document doc, final Path filePath, final String charset,
			final boolean isEscapeText) {
		OutputFormat outputFormat = OutputFormat.createPrettyPrint();
		outputFormat.setEncoding(charset);
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(FileIoUtil.getOutputStream(filePath));
			writer.setEscapeText(isEscapeText);
			writer.write(doc);
		} catch (IOException e) {
			throw new ToolException(StringUtil.format("write xml to file failure,the reason is: {}", e.getMessage()),
					e);
		} finally {
			if (!Objects.isNull(writer)) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new ToolException(
							StringUtil.format("close xml writer  failure,the reason is: {}", e.getMessage()), e);
				}
			}
		}
	}

}
