package uima3;

import java.io.File;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.pear.tools.PackageBrowser;
import org.apache.uima.pear.tools.PackageInstaller;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.ResourceSpecifier;
import org.apache.uima.util.XMLInputSource;


public class PearRunner {

	public static void main(String[] args) {

		try {

			if (args.length < 1) {
				System.err.printf("Usage: %s <path-to-pear-file>\n", PearRunner.class.getSimpleName());
				return;
			}
			
			File pearFile = new File(args[0]);
			File installDir = new File(System.getProperty("java.io.tmpdir"));
			boolean doVerification = true;

			// Install PEAR package
			PackageBrowser instPear = PackageInstaller.installPackage(installDir, pearFile, doVerification);
			String pearDescPath = instPear.getComponentPearDescPath();

			// IMPORTANT: Create a default resouce manager (to avoid weird casting problems in UIMA3/PEAR)
			ResourceManager rsrcMgr = UIMAFramework.newDefaultResourceManager();

			// Create analysis engine from the installed PEAR package using the created PEAR specifier
			XMLInputSource in = new XMLInputSource(pearDescPath);
			ResourceSpecifier specifier = UIMAFramework.getXMLParser().parseResourceSpecifier(in);
			AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(specifier, rsrcMgr, null);
			
			// Create a new JCas
			JCas jcas = ae.newJCas();
			
			// Set text
			jcas.setDocumentLanguage("en");
			jcas.setDocumentText("This is the first line.\nThis is the second one.\nAnd this is the last one.");

			// Process
			ae.process(jcas);

			// Pipeline OK
			System.out.println("Pipeline execution was OK!");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
