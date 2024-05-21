package com.example.cybooks.api;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import com.example.cybooks.model.BookApi;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ParseXML {

    public ParseXML(){

    }

    public List<BookApi> readXML(String xmlString) {
        List<BookApi> bookApis = new ArrayList<BookApi>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)));
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagNameNS("http://www.loc.gov/zing/srw/","recordData");

            for(int i=0; i<nList.getLength(); i++){
                NodeList nodes = nList.item(i).getChildNodes();
                for(int j=0; j<nodes.getLength(); j++){
                    if(nodes.item(j).getNodeName().equals("oai_dc:dc")){
                        BookApi bookApi = new BookApi();
                        NodeList recordDatas = nodes.item(j).getChildNodes();
                        for(int k=0; k<recordDatas.getLength(); k++){
                            switch(recordDatas.item(k).getNodeName()) {
                                case "dc:identifier":
                                    List<String> identifiers = bookApi.getIdentifiers();
                                    if(recordDatas.item(k).getTextContent().contains("ISBN")){
                                        bookApi.setIsbn(recordDatas.item(k).getTextContent().replace("ISBN ", ""));
                                    }else{
                                        identifiers.add(recordDatas.item(k).getTextContent());
                                        bookApi.setIdentifiers(identifiers);
                                    }
                                    break;
                                case "dc:creator":
                                    List<String> authors = bookApi.getAuthors();
                                    authors.add(recordDatas.item(k).getTextContent());
                                    bookApi.setAuthors(authors);
                                    break;
                                case "dc:title":
                                    bookApi.setTitle(recordDatas.item(k).getTextContent());
                                    break;
                                case "dc:publisher":
                                    bookApi.setPublisher(recordDatas.item(k).getTextContent());
                                    break;
                                case "dc:language":
                                    List<String> languages = bookApi.getLanguages();
                                    languages.add(recordDatas.item(k).getTextContent());
                                    bookApi.setLanguages(languages);
                                case "dc:type":
                                    List<String> types = bookApi.getTypes();
                                    types.add(recordDatas.item(k).getTextContent());
                                    bookApi.setTypes(types);
                                    break;
                                case "dc:format":
                                    bookApi.setFormat(recordDatas.item(k).getTextContent());
                                    break;
                                case "dc:date":
                                    bookApi.setDate(recordDatas.item(k).getTextContent());
                                    break;
                                case "dc:rights":
                                    List<String> rights = bookApi.getRights();
                                    rights.add(recordDatas.item(k).getTextContent());
                                    bookApi.setRights(rights);
                                    break;
                                default:
                                    break;
                            }
                        }
                        if(bookApi.getIsbn().isEmpty()){
                            continue;
                        }else{
                            bookApis.add(bookApi);
                        }
                    }
                }
            }

            return bookApis;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
}