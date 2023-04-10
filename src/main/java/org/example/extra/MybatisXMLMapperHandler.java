package org.example.extra;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.constant.MyBatis;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;

public class MybatisXMLMapperHandler implements IResourceHandler {
    private final Logger logger = LogManager.getLogger(MybatisXMLMapperHandler.class);
    private static Map<String, Set<String>> idPlaceholderMap = new HashMap<>();

    @Override
    public void handle(Path filePath) {
        if (!filePath.toFile().getName().endsWith(".xml")) {
            return;
        }
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false); // Disable validation to avoid fetching the DTD
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(new InputSource(inputStream));
            Element root = document.getDocumentElement();

            if (!("mapper".equals(root.getTagName()) && root.hasAttribute("namespace"))) {
                return;
            }

            visitPlaceHolder(null, root, 1);
        } catch (Exception e) {
            // If the file cannot be parsed as an XML document, it's not a MyBatis mapper file
            logger.error(String.format("%s file is not a valid xml file", filePath.toFile().getAbsolutePath()));
        }
    }

    public void visitPlaceHolder(String id, Node node, int depth) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            handleTextNode(id, node);
        } else if (node.getNodeType() == Node.ELEMENT_NODE) {
            if (id == null) {
                id = getElementID(node);
            }
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            visitPlaceHolder(id, childNodes.item(i), depth++);
        }
    }

    public void handleTextNode(String id, Node node) {
        if (id == null) {
            return;
        }

        String nodeContent = node.getNodeValue().trim();
        if (nodeContent.equals("")) {
            return;
        }

        Matcher matcher = MyBatis.mybatisPlaceHolderPattern.matcher(nodeContent);
        if (!matcher.find()) {
            return;
        }

        addPlaceHolder(id, nodeContent);
    }

    public String getElementID(Node node) {
        if (!Arrays.asList("select", "update", "delete", "insert").contains(node.getNodeName())) {
            return null;
        }

        NamedNodeMap namedNodeMap = node.getAttributes();
        Node idNode = namedNodeMap.getNamedItem("id");
        return idNode.getNodeValue();
    }

    public void addPlaceHolder(String id, String placeholder) {
        if (!idPlaceholderMap.containsKey(id)) {
            idPlaceholderMap.put(id, new HashSet<>());
        }

        idPlaceholderMap.get(id).add(placeholder);
    }

    public static Set<String> getPlaceHolder(String id) {
        Set<String> placeHolders = idPlaceholderMap.get(id);
        if (placeHolders == null) {
            placeHolders = Collections.emptySet();
        }
        return placeHolders;
    }
}
