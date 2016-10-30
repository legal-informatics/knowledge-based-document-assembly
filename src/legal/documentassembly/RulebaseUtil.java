package legal.documentassembly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class RulebaseUtil {

	/**
	 * Retrieves facts needed by the rulebase
	 * @return all facts figuring in rulebase
	 */
	public static Map<String,String> retrieveFactsForRulebase() {
		Map<String,String> facts = new HashMap<String,String>();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(EnvironmentProperties.reasoner_path + EnvironmentProperties.rulebase_filename);
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodelist = (NodeList) xpath.evaluate("//*/@*[starts-with(.,'" + EnvironmentProperties.rdf_namespace_prefix + "')]", doc, XPathConstants.NODESET);
			for (int i=0; i<nodelist.getLength(); i++) {
				String factName = nodelist.item(i).getNodeValue();
				factName = factName.substring(EnvironmentProperties.rdf_namespace_prefix.length());
				if (!factName.equals("case"))
					facts.put(factName, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return facts;
	}

	/**
	 * Retrieves possible goals in rulebase
	 * @return names of goals i.e. conclusions
	 */
	public static ArrayList<String> retrieveGoals() {
		ArrayList<String> goals = new ArrayList<String>();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse(EnvironmentProperties.reasoner_path + EnvironmentProperties.rulebase_filename);
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodelist = (NodeList) xpath.evaluate("//*/head/Atom/op/Rel", doc, XPathConstants.NODESET);
			for (int i=0; i<nodelist.getLength(); i++) {
				String goal = nodelist.item(i).getTextContent();
				boolean alreadyFound = false;
				for (String item: goals)
					if (item.equals(goal))
						alreadyFound = true;
				if (!alreadyFound)
					goals.add(goal);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goals;
	}

	
}
