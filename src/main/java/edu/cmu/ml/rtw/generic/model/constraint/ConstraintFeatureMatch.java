package edu.cmu.ml.rtw.generic.model.constraint;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import edu.cmu.ml.rtw.generic.data.annotation.Datum;
import edu.cmu.ml.rtw.generic.data.feature.Feature;
import edu.cmu.ml.rtw.generic.data.feature.FeaturizedDataSet;

/**
 * ConstraintFeatureMatch represents a regex constraint on the names of
 * of the components of a given feature vector computed by a Feature 
 * defined under edu.cmu.ml.rtw.generic.data.feature and reference-able in a FeaturizedDataSet
 * by the name 'featureReference'.  A datum d satisfies this constraint if there
 * is at least one component in the vector computed by the referenced feature whose
 * name matches the the specified regex ('pattern'), and the value of this component
 * for d is at least at the threshold 'minValue'.
 * 
 * @author Bill McDowell
 *
 * @param <D> datum type
 * @param <L> datum label type
 */
public class ConstraintFeatureMatch<D extends Datum<L>, L> extends Constraint<D, L> {
		private String featureReference;
		private double minValue;
		private Pattern pattern;
		
		public ConstraintFeatureMatch(String featureReference, double minValue, String pattern) {
			this.featureReference = featureReference;
			this.minValue = minValue;
			this.pattern = Pattern.compile(pattern);
		}
		
		@Override
		public boolean isSatisfying(FeaturizedDataSet<D, L> data, D datum) {	
			Feature<D, L> feature = data.getFeatureByReferenceName(this.featureReference);
			Map<Integer, Double> featureValues = feature.computeVector(datum); // FIXME Faster to refer to data-set,  but this is fine for now
			Map<Integer, String> vocabulary = feature.getVocabularyForIndices(featureValues.keySet());
			
			for (Entry<Integer, String> entry : vocabulary.entrySet()) {
				if (this.pattern.matcher(entry.getValue()).matches() && 
						featureValues.get(entry.getKey()) >= this.minValue)
					return true;
			}
			
			return false;
		}
		
		public String toString() {
			return "FeatureMatch(" + this.featureReference + ", " + this.minValue + ", " + this.pattern.pattern() + ")";
		}
}
