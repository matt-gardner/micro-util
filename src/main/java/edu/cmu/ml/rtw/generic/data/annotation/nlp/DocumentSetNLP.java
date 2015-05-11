package edu.cmu.ml.rtw.generic.data.annotation.nlp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import edu.cmu.ml.rtw.generic.data.annotation.DocumentSet;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.micro.Annotation;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.micro.DocumentAnnotation;

public class DocumentSetNLP<D extends DocumentNLP> extends DocumentSet<D> {
	public DocumentSetNLP(String name) {
		super(name);
	}

	@Override 
	public DocumentSet<D> makeInstance(String name) {
		return new DocumentSetNLP<D>(name);
	}
	
	public boolean saveToMicroDirectory(String directoryPath, Collection<AnnotationTypeNLP<?>> annotationTypes) {
		for (D document : this.documents.values()) {
			DocumentAnnotation documentAnnotation = document.toMicroAnnotation(annotationTypes);
			documentAnnotation.writeToFile(new File(directoryPath, document.getName()).getAbsolutePath());
		}
		
		return true;
	}
	
	public boolean saveToMicroFile(String filePath, Collection<AnnotationTypeNLP<?>> annotationTypes) {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(filePath));
		
			for (D document : this.documents.values()) {
				DocumentAnnotation documentAnnotation = document.toMicroAnnotation(annotationTypes);
				List<Annotation> annotations = documentAnnotation.getAllAnnotations();
				for (Annotation annotation : annotations) {
					w.write(annotation.toJsonString() + "\n");
				}
			}
			
			w.close();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	public static <D extends DocumentNLP> DocumentSetNLP<D> loadFromMicroPath(String name, String path, D genericDocument) {
		File filePath = new File(path);
		File[] files = null;
		if (filePath.isDirectory()) {
			files = filePath.listFiles();
		} else {
			files = new File[] { filePath };
		}
		
		// FIXME: This assumes that documents are not split across files
		// Might want to remove this assumption...
		DocumentSetNLP<D> documentSet = new DocumentSetNLP<D>(name);
		for (File file : files) {
			documentSet.addAll(loadFromMicroFile(file, genericDocument));
		}
	
		return documentSet;
	}
	
	@SuppressWarnings("unchecked")
	private static <D extends DocumentNLP> DocumentSetNLP<D> loadFromMicroFile(File file, D genericDocument) {
		List<DocumentAnnotation> documentAnnotations = DocumentAnnotation.fromFile(file.getAbsolutePath());
		DocumentSetNLP<D> documentSet = new DocumentSetNLP<D>("");
		for (DocumentAnnotation documentAnnotation : documentAnnotations) {
			documentSet.add((D)genericDocument.makeInstanceFromMicroAnnotation(documentAnnotation));
		}
	
		return documentSet;
	}
}
