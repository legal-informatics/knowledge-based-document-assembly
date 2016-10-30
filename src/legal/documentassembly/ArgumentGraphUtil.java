package legal.documentassembly;

import java.io.FileOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Utility class for building argument graph
 *
 */
public class ArgumentGraphUtil {

	/**
	 * Transforms reasoning proof into argument graph
	 */
	public static void buildArgumentGraph() {
		System.out.println("Generating argument graph...");
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(new StreamSource(EnvironmentProperties.proof2caf_xsl_filename));
			transformer.transform(new StreamSource(EnvironmentProperties.reasoner_path + EnvironmentProperties.proof_filename), new StreamResult(new FileOutputStream(EnvironmentProperties.caf_filename)));
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Error while generating argument graph: " + e.getMessage());
		}
	}

}
