package xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import model.Courier;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import model.DeliveryPoint;
import model.Map;
import model.User;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLdpsSerializer {//implements Visitor{// Singleton

    private Document document;
    private static XMLdpsSerializer instance = null;
    private XMLdpsSerializer(){}
    public static XMLdpsSerializer getInstance(){
            if (instance == null)
                    instance = new XMLdpsSerializer();
            return instance;
    }

    /**
     * Open an XML file and write an XML description of the plan in it 
     * @param map
     * @param user
     * @throws ParserConfigurationException
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     * @throws ExceptionXML
     */
    public void save(Map map, User user) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, ExceptionXML, XPathExpressionException, SAXException, IOException{
        File xml = XMLfileOpener.getInstance().open(false);
        StreamResult result = new StreamResult(xml);
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        createMapsElt(map, user);
        DOMSource source = new DOMSource(document);
        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        //xformer.setOutputProperty(OutputKeys.INDENT, "yes");
        xformer.transform(source, result);
    }

    private void createMapsElt(Map map, User user) throws XPathExpressionException {
        NodeList mapsList = document.getElementsByTagName("maps");
        Element maps;
        if (mapsList.getLength()==0) {
            maps = document.createElement("maps");
            document.appendChild(maps);
        }else{
            maps = (Element) mapsList.item(0);
        }
        
        String expression = "/maps/map[@src='" + map.getMapName() + "']";
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression xPathExpression = xPath.compile(expression);
        Element eltMap = (Element) xPathExpression.evaluate(document, XPathConstants.NODE);

        if (eltMap != null) {
            removeChildren(eltMap);            
        }
        
        else {
            eltMap = document.createElement("map");
            createAttribute(eltMap, "src", map.getMapName());
        }

        for (Long idCourier : user.getListCourier().keySet()) {
            Courier c = user.getCourierById(idCourier);
            Element eltCourier = createCourierNode(c);

            for (int i =1; i < c.getCurrentDeliveryPoints().size(); i++) {
                DeliveryPoint dp = c.getCurrentDeliveryPoints().get(i);
                Element eltDP = createDeliveryPointNode(dp);
                eltCourier.appendChild(eltDP);
            }

            eltMap.appendChild(eltCourier);
        }    
        maps.appendChild(eltMap);
    }

    private void createAttribute(Element root, String name, String value){
    	Attr attribut = document.createAttribute(name);
    	root.setAttributeNode(attribut);
    	attribut.setValue(value);
    }
    
    private Element createCourierNode(Courier c) {
        Element eltCourier = document.createElement("courier");
        createAttribute(eltCourier, "id", c.getId().toString());
        return eltCourier;
    }
    
    private Element createDeliveryPointNode(DeliveryPoint dp) {
        Element eltDeliveryPoint = document.createElement("deliveryPoint");
        createAttribute(eltDeliveryPoint, "id", dp.getId().toString());
        createAttribute(eltDeliveryPoint,"timeWindow", dp.getTimeWindow().toString());
        return eltDeliveryPoint;
    }
    
    private void removeChildren(Node node) {
        while (node.hasChildNodes())
           node.removeChild(node.getFirstChild());
    }
    
}
