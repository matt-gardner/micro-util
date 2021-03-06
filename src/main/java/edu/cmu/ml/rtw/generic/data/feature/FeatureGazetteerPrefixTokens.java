package edu.cmu.ml.rtw.generic.data.feature;

import java.util.Arrays;
import java.util.List;

import edu.cmu.ml.rtw.generic.data.Context;
import edu.cmu.ml.rtw.generic.data.DataTools;
import edu.cmu.ml.rtw.generic.data.annotation.Datum;
import edu.cmu.ml.rtw.generic.parse.Obj;
import edu.cmu.ml.rtw.generic.util.Pair;
import edu.cmu.ml.rtw.generic.util.StringUtil;

/**
 * For datum d, string extractor S, and gazetteer G, 
 * FeatureGazetteerPrefixTokens computes
 * 
 * max_{g\in G} 1(S(d) shares k prefix tokens with G)
 * 
 * The value of k is determined by the parameter 'minTokens'
 * 
 * @author Bill McDowell
 *
 * @param <D> datum type
 * @param <L> datum label type
 *
 */
public class FeatureGazetteerPrefixTokens<D extends Datum<L>, L> extends FeatureGazetteer<D, L> {
	private DataTools.StringPairMeasure prefixTokensMeasure;
	private int minTokens;
	
	public FeatureGazetteerPrefixTokens() {
		
	}
	
	public FeatureGazetteerPrefixTokens(Context<D, L> context) {
		super(context);
		
		this.extremumType = FeatureGazetteer.ExtremumType.Maximum;
		
		this.prefixTokensMeasure = new DataTools.StringPairMeasure() {
			public double compute(String str1, String str2) {
				return StringUtil.prefixTokenOverlap(str1, str2);
			}
		};
		
		this.minTokens = 2;
		
		this.parameterNames = Arrays.copyOf(this.parameterNames, this.parameterNames.length + 1);
		this.parameterNames[this.parameterNames.length - 1] = "minTokens";
	}
	
	@Override
	protected Pair<List<Pair<String,Double>>, Double> computeExtremum(String str) {
		Pair<List<Pair<String,Double>>, Double> idsAndTokenPrefixCount = this.gazetteer.max(str, this.prefixTokensMeasure);
		
		if (idsAndTokenPrefixCount.getSecond() >= this.minTokens)
			return new Pair<List<Pair<String,Double>>, Double>(idsAndTokenPrefixCount.getFirst(), 1.0);
		else
			return new Pair<List<Pair<String,Double>>, Double>(idsAndTokenPrefixCount.getFirst(), 0.0);
	}

	@Override
	public String getGenericName() {
		return "GazetteerPrefixTokens";
	}

	@Override
	public Feature<D, L> makeInstance(Context<D, L> context) {
		return new FeatureGazetteerPrefixTokens<D, L>(context);
	}

	@Override
	public Obj getParameterValue(String parameter) {
		Obj parameterValue = super.getParameterValue(parameter);
		if (parameterValue != null)
			return parameterValue;
		else if (parameter.equals("minTokens"))
			return Obj.stringValue(String.valueOf(this.minTokens));
		return null;
	}

	@Override
	public boolean setParameterValue(String parameter, Obj parameterValue) {
		if (super.setParameterValue(parameter, parameterValue))
			return true;
		else if (parameter.equals("minTokens"))
			this.minTokens = Integer.valueOf(this.context.getMatchValue(parameterValue));
		else
			return false;
		
		return true;
	}
}
