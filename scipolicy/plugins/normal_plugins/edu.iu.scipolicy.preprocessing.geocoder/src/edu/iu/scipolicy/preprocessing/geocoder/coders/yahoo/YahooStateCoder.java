package edu.iu.scipolicy.preprocessing.geocoder.coders.yahoo;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import edu.iu.scipolicy.preprocessing.geocoder.coders.yahoo.placefinder.PlaceFinderClient;
import edu.iu.scipolicy.preprocessing.geocoder.coders.yahoo.placefinder.beans.ResultSet;

public class YahooStateCoder extends AbstractYahooCoder {
	
	public YahooStateCoder(String applicationId) {
		super(applicationId);
	}

	@Override
	public CODER_TYPE getLocationType() {
		return CODER_TYPE.US_STATE;
	}

	@Override
	public ResultSet requestYahooService(String location, String applicationId)
											throws IOException, JAXBException {
		return PlaceFinderClient.requestState(location, "US", applicationId);
	}
}