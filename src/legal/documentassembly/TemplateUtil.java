package legal.documentassembly;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import legal.documentassembly.bean.Exercise;
import legal.documentassembly.bean.Step;
import toxgene.core.Engine;
import toxgene.interfaces.ToXgeneDocumentCollection;
import toxgene.interfaces.ToXgeneReporter;
import toxgene.interfaces.ToXgeneSession;
import toxgene.util.ToXgeneReporterImpl;

/**
 * Utility class for operations with document template
 *
 */
public class TemplateUtil {

	/**
	 * Executes building of final document
	 * @param exercise Exercise object containing answers i.e. gathered facts
	 */
	public static void buildDocument(Exercise exercise) {
		System.out.println("Exporting facts for document building...");
		exportFacts(exercise);
		System.out.println("Building the document...");
		try {
			System.setProperty("ToXgene_home","./lib");
			boolean verbose = false;
			boolean showWarnings = true;
			ToXgeneReporter tgReporter = new ToXgeneReporterImpl(verbose, showWarnings); 
			ToXgeneSession session = new ToXgeneSession();
			session.reporter = tgReporter;
			session.addNewLines = true;
			toxgene.core.Engine tgEngine = new Engine();
			tgEngine.startSession(session);
			tgEngine.parseTemplate(new FileInputStream(EnvironmentProperties.input_folder + exercise.getTemplateFilename()));
			ToXgeneDocumentCollection tgCollection = (ToXgeneDocumentCollection)tgEngine.getToXgeneDocumentCollections().get(0);
			tgEngine.materialize(tgCollection, new PrintStream(new FileOutputStream(EnvironmentProperties.document_filename)));
			//toxgene.ToXgene.main(arg0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Prepares facts in XML format recognized by the template engine
	 * @param exercise Exercise object containing answers i.e. gathered facts
	 */
	public static void exportFacts(Exercise exercise) {
		String text = "<fact_list>\r\n";
		for (Step question: exercise.getSteps())
			text += "<fact><name>" + question.getTemplateFact() + "</name><value>" + question.getAnswer().toString() + "</value></fact>\r\n";
		text += "</fact_list>";
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(EnvironmentProperties.document_facts_filename));
			out.print(text);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}
	}

	/**
	 * Retrieves facts needed by the document template
	 * @param templateFilename filename of the document template
	 * @return retrieved fact names
	 */
	public static Map<String,String> retrieveFactsForTemplate(String templateFilename) {
		HashMap<String,String> retVal = new HashMap<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(EnvironmentProperties.input_folder + templateFilename));
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			try {
				while((line = reader.readLine()) != null)
					stringBuilder.append(line);
			} finally {
				reader.close();
			}
			String regex = "where=\"EQ\\(\\[name\\]\\,\\'([a-zA-Z_$][a-zA-Z_$0-9]*)\\'\\)\""; //   "\\b(\\d{3})(\\d{3})(\\d{4})\\b";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(stringBuilder.toString());
			while (matcher.find())
				retVal.put(matcher.group(1), "string");
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		return retVal;
	}

}
