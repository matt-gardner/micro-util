package edu.cmu.ml.rtw.generic.data.feature;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.ml.rtw.generic.cluster.Clusterer;
import edu.cmu.ml.rtw.generic.data.Context;
import edu.cmu.ml.rtw.generic.data.annotation.Datum;
import edu.cmu.ml.rtw.generic.data.annotation.nlp.TokenSpan;
import edu.cmu.ml.rtw.generic.parse.Obj;

public class FeatureGramCluster<D extends Datum<L>, L> extends FeatureGram<D, L> {	
	protected Clusterer<TokenSpan> clusterer;
	
	public FeatureGramCluster() {
		
	}
	
	public FeatureGramCluster(Context<D, L> context) {
		super(context);
		
		this.clusterer = null;
		this.parameterNames = Arrays.copyOf(this.parameterNames, this.parameterNames.length + 1);
		this.parameterNames[this.parameterNames.length - 1] = "clusterer";
	}


	@Override
	public Obj getParameterValue(String parameter) {
		Obj parameterValue = super.getParameterValue(parameter);
		if (parameterValue != null)
			return parameterValue;
		else if (parameter.equals("clusterer"))
			return Obj.stringValue((this.clusterer == null) ? "None" : this.clusterer.getName());
		return null;
	}

	@Override
	public boolean setParameterValue(String parameter, Obj parameterValue) {
		if (super.setParameterValue(parameter, parameterValue))
			return true;
		else if (parameter.equals("clusterer"))
			this.clusterer = this.context.getDatumTools().getDataTools().getTokenSpanClusterer(this.context.getMatchValue(parameterValue));
		else
			return false;
		
		return true;
	}
	
	@Override
	protected Map<String, Integer> getGramsForDatum(D datum) {
		TokenSpan[] tokenSpans = this.tokenExtractor.extract(datum);
		Map<String, Integer> retGrams = new HashMap<String, Integer>();
		
		for (TokenSpan tokenSpan : tokenSpans) {			
			List<String> clusters = this.clusterer.getClusters(tokenSpan);
			
			for (String cluster : clusters) {
				if (!retGrams.containsKey(cluster))
					retGrams.put(cluster, 1);
				else
					retGrams.put(cluster, retGrams.get(cluster) + 1);
			}
		}
			
		return retGrams;
	}
	
	@Override
	public String getGenericName() {
		return "GramCluster";
	}

	@Override
	public Feature<D, L> makeInstance(Context<D, L> context) {
		return new FeatureGramCluster<D, L>(context);
	}
}
