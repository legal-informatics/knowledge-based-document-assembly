package legal.documentassembly;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import legal.documentassembly.bean.Exercise;
import legal.documentassembly.bean.RuleFact;
import legal.documentassembly.bean.Step;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
/**
 * Utility class for reasoning activities
 *
 */
public class ReasonerUtil {

	/**
	 * Retrieval of reasoning conclusion
	 * @param exercise Exercise object containing answers i.e. gathered facts
	 * @return Reasoning result if conclusion is (positively/negatively) (defeasibly/definitely) proven
	 */
	public static String getConclusion(Exercise exercise) {
		System.out.println("Exporting facts for reasoning...");
		updateRdf(exercise);
		System.out.println("Preparing rulebase for reasoner...");
		buildRulebase(exercise);
		System.out.println("Setting reasoning goal...");
		updateGoal(exercise);
		System.out.println("Starting the reasoner...");
		startReasoner();
		System.out.println("Retrieving reasoning result...");
		return retrieveResult();
	}
	
	/**
	 * Exports facts to rdf file
	 * @param exercise Exercise object containing answers i.e. gathered facts
	 */
	private static void updateRdf(Exercise exercise) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(EnvironmentProperties.reasoner_path + EnvironmentProperties.rdf_filename);
			for (Step step: exercise.getSteps()) {
				String rdfProperty = "";
				for (RuleFact ruleFact: exercise.getRuleFacts())
					if (ruleFact.getName().equals(step.getRuleFact()))
						rdfProperty = ruleFact.getName();
				Node node = doc.getElementsByTagName(EnvironmentProperties.rdf_namespace_prefix + rdfProperty).item(0);
				if (node != null)
					node.setTextContent(step.getAnswer());
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(EnvironmentProperties.reasoner_path + EnvironmentProperties.rdf_filename));
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets conclusion to be tested by the reasoner
	 * @param exercise Exercise object containing answers i.e. gathered facts
	 */
	private static void updateGoal(Exercise exercise) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(EnvironmentProperties.reasoner_path + EnvironmentProperties.rulebase_filename);
			Node node = doc.getElementsByTagName("RuleML").item(0);
			node.getAttributes().getNamedItem("rdf_export_classes").setNodeValue(exercise.getRulebaseGoal());
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(EnvironmentProperties.reasoner_path + EnvironmentProperties.rulebase_filename));
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Transforms rulebase into format recognized by the reasoner
	 * @param exercise Exercise object containing answers i.e. gathered facts
	 */
	private static void buildRulebase(Exercise exercise) {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);
			Transformer transformer = tFactory.newTransformer(new StreamSource(EnvironmentProperties.lrml2drdevice_xsl_filename));
			transformer.transform(new StreamSource(EnvironmentProperties.lrml_rulebase_filename), new StreamResult(new FileOutputStream(EnvironmentProperties.reasoner_path + EnvironmentProperties.rulebase_filename)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Executes reasoning
	 * @return if execution was successful
	 */
	private static boolean startReasoner() {
		try {
			prepareClipsdos();
			Process process = Runtime.getRuntime().exec(EnvironmentProperties.reasoner_path + EnvironmentProperties.start_reasoner_command, null, new File(EnvironmentProperties.reasoner_path));
			process.waitFor();
			if (process.exitValue() == 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Retrieves reasoning result
	 * @return Reasoning result if conclusion is (positively/negatively) (defeasibly/definitely) proven
	 */
	private static String retrieveResult() {
		String result = "";
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(EnvironmentProperties.reasoner_path + EnvironmentProperties.result_filename);
			XPath xpath = XPathFactory.newInstance().newXPath();
			result = (String) xpath.evaluate("//*[local-name()='truthStatus']/text()", doc, XPathConstants.STRING);
			System.out.println(result);
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Error while retrieving reasoning result: " + e.getMessage());
		}
		return result;
	}
	
	/**
	 * Places appropriate clipsdos interpreter (32 bit or 64 bit) for reasoner
	 * @throws IOException
	 */
	public static void prepareClipsdos() throws IOException {
		String inputFile = EnvironmentProperties.clipsdos32_filename;
		if (System.getenv("ProgramFiles(x86)") != null) { // 64-bit OS
			inputFile = EnvironmentProperties.clipsdos64_filename;
		}
		InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(inputFile);
	        os = new FileOutputStream(EnvironmentProperties.clipsdos_filename);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	        is.close();
	        os.close();
	    }
	}

}
