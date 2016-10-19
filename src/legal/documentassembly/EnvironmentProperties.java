package legal.documentassembly;

public class EnvironmentProperties {

	public static String input_folder = "input\\";
	public static String output_folder = "output\\";
	public static String temp_folder = "temp\\";
	
	//public static String reasoner_path = "d:\\test\\legalruleml\\reasoner\\";
	public static String reasoner_path = "f:\\temp\\legalruleml\\reasoner\\";
	public static String lrml_rulebase_filename = input_folder + "rulebase.xml";
	public static String lrml2drdevice_xsl_filename = input_folder + "lrml2dr-device.xsl";
	public static String rulebase_filename = "rulebase.ruleml";
	public static String rdf_filename = "facts.rdf";
	public static String rdf_namespace_prefix = "dd:";
	public static String result_filename = "export.rdf";
	public static String proof_filename = "proof.ruleml";
	public static String start_reasoner_command = "start.bat";
	public static String clipsdos32_filename = reasoner_path + "clipsdos\\clipsdos32.exe";
	public static String clipsdos64_filename = reasoner_path + "clipsdos\\clipsdos64.exe";
	public static String clipsdos_filename = reasoner_path + "clipsdos\\clipsdos.exe";
	
	public static String caf_filename = output_folder + "caf.xml";
	public static String proof2caf_xsl_filename = input_folder + "proof2caf.xsl";
	public static String document_facts_filename = temp_folder + "facts.xml";
	public static String document_filename = output_folder + "indictment-document.xml";
	
}
