package edu.iu.scipolicy.preprocessing.aggregatedata.aggregators;

import java.util.Collections;
import java.util.List;

import edu.iu.scipolicy.preprocessing.aggregatedata.SingleFunctionAggregator;

public class MaxAggregator implements SingleFunctionAggregator {

	public Object aggregateValue(List objectsToAggregate) {
		return (Number) Collections.max(objectsToAggregate);
	}

}
