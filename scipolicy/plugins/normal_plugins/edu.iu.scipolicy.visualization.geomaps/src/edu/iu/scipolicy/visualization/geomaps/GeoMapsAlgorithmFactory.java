package edu.iu.scipolicy.visualization.geomaps;

import java.util.Collection;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.ParameterMutator;
import org.cishell.framework.data.Data;
import org.cishell.reference.service.metatype.BasicAttributeDefinition;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/*
 * TODO
 * 
 * Find a lean, clean United States shapefile to replace shapefiles/statesp020.*.
 * We're down from 10MB to 6MB.  Can we do better?
 * 
 * File extension is "ps" even though the metadata says "eps".  We'd have to write
 * some (trivial) new code that takes the eps MIME type to file-ext eps.  Worth it?
 * 
 * The PostScript color gradient drawn by LabeledGradient needs to reflect the scaling.
 * When scale is log, the gradient should appear logged.  The PostScript code to change
 * is in printing/legendPostScriptDefinitions.ps (see function labeledgradient).
 * 
 * The legend components currently will include data in its extrema even when that
 * piece of data isn't visible on the map.  For example, if the map is of the United
 * States, but the input data included figures for Egypt, then if the Egypt data
 * represents extremes in the data, then that will be reflected in the legend even
 * though you can't see Egypt on the output map.  Like if Egypt has a circle size
 * of 10000, and all the United States figures are less than 100, then the circle size
 * legend component will show the maximum as 10000 and show a circle of that size even
 * though no circle of that size is visible.  But is this generally wrong?  Perhaps
 * the user wants the Egyptian extreme to skew the US visualizations.
 * Need an executive call on this one.
 */

public abstract class GeoMapsAlgorithmFactory implements AlgorithmFactory, ParameterMutator {
	@SuppressWarnings("unchecked") // TODO
	public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new GeoMapsAlgorithm(data, parameters, context, getAnnotationMode());
    }
    
    protected abstract AnnotationMode getAnnotationMode();
    
    public abstract ObjectClassDefinition mutateParameters(Data[] data, ObjectClassDefinition oldParameters);
    
    protected static AttributeDefinition formStringDropdownAttributeDefinition(AttributeDefinition oldAttributeDefinition, Collection<String> options) {
    	String[] optionsArray = (String[]) options.toArray(new String[options.size()]);
    	
		AttributeDefinition stringAttributeDefinition =
			new BasicAttributeDefinition(oldAttributeDefinition.getID(),
										 oldAttributeDefinition.getName(),
										 oldAttributeDefinition.getDescription(),
										 AttributeDefinition.STRING,
										 optionsArray,
										 optionsArray);
	
		return stringAttributeDefinition;
	}  
}