package uima3;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

/**
 * Index a DocumentAnnotation covering the whole text.
 */
public class DocumentAnnotantor extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		
		DocumentAnnotation docAnno = new DocumentAnnotation(jcas);
		docAnno.setBegin(0);
		docAnno.setEnd(jcas.getDocumentText().length());
		docAnno.addToIndexes();
	}

}
