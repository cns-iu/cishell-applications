package edu.iu.scipolicy.preprocessing.aggregatedata.aggregators;

import java.util.List;

import edu.iu.scipolicy.preprocessing.aggregatedata.SingleFunctionAggregator;

public class DoubleDifferenceAggregator implements SingleFunctionAggregator<Double> {

	public Double aggregateValue(List<Double> objectsToAggregate) {
		Double currentDifferenceValue = new Double(0);
		
		for (Double currentValue : objectsToAggregate) {
			currentDifferenceValue = currentValue.doubleValue()
											- currentDifferenceValue.doubleValue();
		}
		
		return currentDifferenceValue.doubleValue();
	}

}
