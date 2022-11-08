package xml;

import java.io.File;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import model.Circle;
import model.Visitor;
import model.Plan;
import model.Rectangle;
import model.Shape;

public class XMLserializer implements Visitor{// Singleton
	
	private Element shapeRoot;
	private Document document;
	private static XMLserializer instance = null;
	private XMLserializer(){}
	public static XMLserializer getInstance(){
		if (instance == null)
			instance = new XMLserializer();
		return instance;
	}
 
	/**
	 * Open an XML file and write an XML description of the plan in it 
	 * @param plan the plan to serialise
	 * @throws ParserConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws ExceptionXML
	 */
	public void save(Plan plan) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, ExceptionXML{
            File xml = XMLfileOpener.getInstance().open(false);
            StreamResult result = new StreamResult(xml);
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.appendChild(createPlanElt(plan));
            DOMSource source = new DOMSource(document);
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");
            xformer.transform(source, result);
	}

	private Element createPlanElt(Plan plan) {
            Element racine = document.createElement("plan");
            createAttribute(racine, "height", Integer.toString(plan.getHeight()));
            createAttribute(racine, "width", Integer.toString(plan.getWidth()));
            Iterator<Shape> it = plan.getShapeIterator();
            while (it.hasNext()) {
                it.next().display(this);
                racine.appendChild(shapeRoot);
            }
            return racine;
	}
	
    private void createAttribute(Element root, String name, String value){
    	Attr attribut = document.createAttribute(name);
    	root.setAttributeNode(attribut);
    	attribut.setValue(value);
    }
   
	@Override
	public void display(Circle c) {
        shapeRoot = document.createElement("circle");
        createAttribute(shapeRoot,"x",Integer.toString(c.getCenter().getX()));
        createAttribute(shapeRoot,"y",Integer.toString(c.getCenter().getY()));
        createAttribute(shapeRoot,"radius",Integer.toString(c.getRadius()));
	}

	@Override
	public void display(Rectangle r) {
        shapeRoot = document.createElement("rectangle");
        createAttribute(shapeRoot,"x",Integer.toString(r.getCorner().getX()));
        createAttribute(shapeRoot,"y",Integer.toString(r.getCorner().getY()));
        createAttribute(shapeRoot,"width",Integer.toString(r.getWidth()));
        createAttribute(shapeRoot,"height",Integer.toString(r.getHeight()));
	}
}
