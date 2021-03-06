package edu.cmu.ml.rtw.generic.model.annotator.nlp;

import java.util.List;
import java.util.Map;

import edu.cmu.ml.rtw.generic.data.annotation.AnnotationType;
import edu.cmu.ml.rtw.generic.data.annotation.Document;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.AnnotationTypeNLP;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.DocumentNLP;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.TokenSpan;
import edu.cmu.ml.rtw.generic.model.annotator.Annotator;
import edu.cmu.ml.rtw.generic.util.Pair;
import edu.cmu.ml.rtw.generic.util.Triple;

public class PipelineNLPWelded extends PipelineNLP {
	private PipelineNLP first;
	private PipelineNLP second;
	
	public PipelineNLPWelded(PipelineNLP first, PipelineNLP second) {
		super();
		
		for (int i = 0; i < first.getAnnotatorCount(); i++) {
			if (second.hasAnnotator(first.getAnnotationType(i)))
				throw new IllegalArgumentException("Cannot weld pipelines containing annotators for the same annotation types");
		}
		
		this.first = first;
		this.second = second;
	}
	
	public String getAnnotatorName(AnnotationType<?> annotationType) {
		if (this.first.hasAnnotator(annotationType))
			return this.first.getAnnotatorName(annotationType);
		else
			return this.second.getAnnotatorName(annotationType);
	}
	
	public boolean hasAnnotator(AnnotationType<?> annotationType) {
		return this.first.hasAnnotator(annotationType) || this.second.hasAnnotator(annotationType);
	}
	
	public boolean annotatorMeasuresConfidence(AnnotationType<?> annotationType) {
		if (this.first.hasAnnotator(annotationType))
			return this.first.annotatorMeasuresConfidence(annotationType);
		else
			return this.second.annotatorMeasuresConfidence(annotationType);
	}
	
	public boolean meetsAnnotatorRequirements(AnnotationType<?> annotationType, Document document) {
		if (!hasAnnotator(annotationType))
			return false;
		
		if (this.first.hasAnnotator(annotationType))
			return this.first.meetsAnnotatorRequirements(annotationType, document);
		else
			return this.second.meetsAnnotatorRequirements(annotationType, document);
	}
	
	public int getAnnotatorCount() {
		return this.first.getAnnotatorCount() + this.second.getAnnotatorCount();
	}
	
	public AnnotationType<?> getAnnotationType(int index) {
		if (index < this.first.getAnnotatorCount())
			return this.first.getAnnotationType(index);
		else 
			return this.second.getAnnotationType(index - this.first.getAnnotatorCount());
	}
	
	protected void addAnnotator(AnnotationType<?> annotationType, Annotator<?> annotator) {
		throw new UnsupportedOperationException();
	}
	
	protected void clearAnnotators() {
		throw new UnsupportedOperationException();
	}
	
	public boolean setDocument(DocumentNLP document) {
		return this.first.setDocument(document) && this.second.setDocument(document);
	}
	
	public <T> Pair<T, Double> annotateDocument(AnnotationTypeNLP<T> annotationType) {
		if (this.first.hasAnnotator(annotationType))
			return this.first.annotateDocument(annotationType);
		else
			return this.second.annotateDocument(annotationType);
	}
	
	public <T> Map<Integer, Pair<T, Double>> annotateSentences(AnnotationTypeNLP<T> annotationType) {
		if (this.first.hasAnnotator(annotationType))
			return this.first.annotateSentences(annotationType);
		else
			return this.second.annotateSentences(annotationType);
	}
	
	public <T> List<Triple<TokenSpan, T, Double>> annotateTokenSpans(AnnotationTypeNLP<T> annotationType) {
		if (this.first.hasAnnotator(annotationType))
			return this.first.annotateTokenSpans(annotationType);
		else
			return this.second.annotateTokenSpans(annotationType);
	}
	
	public <T> Pair<T, Double>[][] annotateTokens(AnnotationTypeNLP<T> annotationType) {
		if (this.first.hasAnnotator(annotationType))
			return this.first.annotateTokens(annotationType);
		else
			return this.second.annotateTokens(annotationType);
	}
}
