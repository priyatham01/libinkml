
package ch.unibe.eindermu.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Marcus Liwicki
 */
public class XmlHandler{
    
    private Document document = null;
    
    private File schema = null;
     
    public void setSchema(File file) {
        schema = file;
    }
    
    public void loadFromFile(File file) throws IOException {
    	loadFromStream(new FileInputStream(file));
    }
    
    public void loadFromStream(InputStream file) throws IOException {

        try {
            // Find a parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            // factory.setValidating(true);
            
            DocumentBuilder parser = factory.newDocumentBuilder();
            
            // Read the document
            
            document = parser.parse(file);
            
            if(schema != null){
                SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                System.err.println(schema.getPath());
                System.err.println(getClass().toString());
                Schema schemaObject = schemaFactory.newSchema(schema);
                Validator validator = schemaObject.newValidator(); 
                DOMSource source = new DOMSource(document);
                DOMResult result = new DOMResult();
                try{
                    validator.validate(source,result);
                    document = (Document) result.getNode();
                } catch(SAXException e){
                    throw new IOException(e.getMessage());
                }
            }
            
        } catch(SAXException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        } catch(ParserConfigurationException e) {
            throw new IOException(e.getMessage());
        }
    }
    
    public void createNewDocument() throws ParserConfigurationException {
        // Find a parser
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder parser = factory.newDocumentBuilder();
        // Read the document
        this.document = parser.newDocument();
        
    }
    
    public void saveToFile(File file) throws TransformerException {
        // Write it out again
        TransformerFactory xformFactory = TransformerFactory.newInstance();
        Transformer idTransform = xformFactory.newTransformer();
        Source input = new DOMSource(document);
        Result output = new StreamResult(file);
        idTransform.transform(input, output);
    }
    
    public void saveToStream(OutputStream stream) throws TransformerException{
        TransformerFactory xformFactory = TransformerFactory.newInstance();
        Transformer idTransform = xformFactory.newTransformer();
        Source input = new DOMSource(document);
        Result output = new StreamResult(stream);
        idTransform.transform(input, output);
    }
    
	public Document getDocument() {
		return this.document;
	}    
}
