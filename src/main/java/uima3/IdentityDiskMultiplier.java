package uima3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.AbstractCas;
import org.apache.uima.fit.component.JCasMultiplier_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasIOUtils;


/**
 * Save the input CAS to disk, and produce a new CAS loading the previous one from disk.
 * 
 * NOTE: This multiplier was coded only to show a bug. It does not have much sense. 
 */
public class IdentityDiskMultiplier extends JCasMultiplier_ImplBase {

	private File xmiFile = null;
	
	protected void save(JCas jcas, File file) throws IOException {
        
        try (FileOutputStream fos = new FileOutputStream(file)) {

            CasIOUtils.save(jcas.getCas(), fos, org.apache.uima.cas.SerialFormat.XMI);

            System.out.println(String.format("File %s has been written", file.getAbsolutePath()));
        }
	}
	
	protected JCas load(File file) throws IOException {
        
    	JCas newCas = null;
        try (FileInputStream fis = new FileInputStream(file)){
            newCas = getEmptyJCas();
            
            CasIOUtils.load(fis, newCas.getCas());

            System.out.println(String.format("JCas from file %s has been loaded", file.getAbsolutePath()));

            return newCas;

        } catch (IOException | RuntimeException e) {
        	newCas.release();
            throw e;
        }
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {

        Path tmpDir = null;
        try {
        
        	// Chose a location to save the xmi
        	tmpDir = Files.createTempDirectory("xmi-test-");
        	xmiFile = tmpDir.resolve(Paths.get("test.xmi")).toFile();
        	
        	// Save the xmi
        	save(jcas, xmiFile);

        } catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
	}

	@Override
	public boolean hasNext() throws AnalysisEngineProcessException {
		return xmiFile != null;
	}

	@Override
	public AbstractCas next() throws AnalysisEngineProcessException {
		
		if (xmiFile == null) {
			return null;
		}
		
        try {
        	// Create a new JCas loading the XMI from the previous one
        	return load(xmiFile);

        } catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        } finally {
			xmiFile = null;
		}
		
	}

}
