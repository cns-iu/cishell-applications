package edu.iu.sci2.database.star.extract.network;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmCreationCanceledException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;
import org.cishell.utility.datastructure.datamodel.group.DataModelGroup;
import org.cishell.utility.swt.GUICanceledException;
import org.cishell.utility.swt.model.SWTModel;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import edu.iu.sci2.database.star.common.StarDatabaseMetadata;
import edu.iu.sci2.database.star.extract.common.ExtractionAlgorithmFactory;
import edu.iu.sci2.database.star.extract.common.StarDatabaseDescriptor;
import edu.iu.sci2.database.star.extract.common.guibuilder.GUIBuilder;
import edu.iu.sci2.database.star.extract.network.guibuilder.TwoLeafTableNetworkGUIBuilder;
import edu.iu.sci2.database.star.extract.network.query.CoreToLeafDirectedNetworkQueryConstructor;
import edu.iu.sci2.database.star.extract.network.query.LeafToCoreDirectedNetworkQueryConstructor;
import edu.iu.sci2.database.star.extract.network.query.LeafToLeafDirectedNetworkQueryConstructor;
import edu.iu.sci2.database.star.extract.network.query.NetworkQueryConstructor;

// TODO: Rename this at some point to reflect bipartite terminology.
public abstract class ExtractTwoLeafTableNetworkAlgorithmFactory
		extends ExtractionAlgorithmFactory {
	public static final String WINDOW_TITLE = "Extract Bipartite Network";

	private BundleContext bundleContext;
	private LogService logger;

	protected void activate(ComponentContext componentContext) {
		this.bundleContext = componentContext.getBundleContext();
		this.logger = (LogService)componentContext.locateService("LOG");
	}

    public Algorithm createAlgorithm(
    		Data[] data, Dictionary<String, Object> parameters, CIShellContext ciShellContext) {
    	Data parentData = data[0];
    	StarDatabaseMetadata databaseMetadata = getMetadata(parentData);
    	verifyLeafTables(databaseMetadata, this.logger);
    	SWTModel model = getModelFromUser(databaseMetadata);
    	NetworkQueryConstructor queryConstructor = decideQueryConstructor(databaseMetadata, model);
    	AlgorithmFactory networkQueryRunner = getNetworkQueryRunner(this.bundleContext);

        return new ExtractNetworkAlgorithm(
        	ciShellContext, parentData, queryConstructor, networkQueryRunner, this.logger);
    }

    public int minimumLeafTableCount() {
    	return 1;
    }

    public String extractionType() {
    	return NETWORK_EXTRACTION_TYPE;
    }

    public abstract String instructionsLabelText();
	public abstract String tutorialURL();
	public abstract String tutorialDisplayURL();
	public abstract int instructionsLabelHeight();
	public abstract String sourceLeafFieldLabel();
	public abstract String sourceLeafFieldName();
	public abstract String targetLeafFieldLabel();
	public abstract String targetLeafFieldName();

    public SWTModel getModelFromUser(StarDatabaseMetadata metadata)
    		throws AlgorithmCreationCanceledException {
    	try {
    		return new TwoLeafTableNetworkGUIBuilder(
    			instructionsLabelText(),
    			tutorialURL(),
    			tutorialDisplayURL(),
    			instructionsLabelHeight(),
    			sourceLeafFieldLabel(),
    			sourceLeafFieldName(),
    			targetLeafFieldLabel(),
    			targetLeafFieldName()).createGUI(
    			WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT, new StarDatabaseDescriptor(metadata));
    	} catch (GUICanceledException e) {
    		throw new AlgorithmCreationCanceledException(e.getMessage(), e);
    	} catch (UniqueNameException e) {
    		throw new AlgorithmCreationCanceledException(e.getMessage(), e);
    	}
    }

    private NetworkQueryConstructor decideQueryConstructor(
    		StarDatabaseMetadata metadata, SWTModel model) {
    	DataModelGroup headerGroup = model.getGroup(GUIBuilder.HEADER_GROUP_NAME);
    	DataModelField<?> entity1 =
    		headerGroup.getField(sourceLeafFieldName());
    	DataModelField<?> entity2 =
    		headerGroup.getField(targetLeafFieldName());
    	String coreEntityTableName = metadata.getCoreEntityTableName();
    	String entity1Value = (String) entity1.getValue();
    	String entity2Value = (String) entity2.getValue();

    	// Leaf -> Core
    	if (coreEntityTableName.equals(entity2Value)) {
    		return new LeafToCoreDirectedNetworkQueryConstructor(
    			entity1Value,
    			GUIBuilder.HEADER_GROUP_NAME,
    			GUIBuilder.NODE_ATTRIBUTE_FUNCTION_GROUP_NAME,
    			GUIBuilder.NODE_CORE_ENTITY_COLUMN_GROUP_NAME,
    			GUIBuilder.NODE_ATTRIBUTE_NAME_GROUP_NAME,
    			GUIBuilder.EDGE_ATTRIBUTE_FUNCTION_GROUP_NAME,
    			GUIBuilder.EDGE_CORE_ENTITY_COLUMN_GROUP_NAME,
    			GUIBuilder.EDGE_ATTRIBUTE_NAME_GROUP_NAME,
    			model,
    			metadata);
    	// Core -> Leaf
    	} else if (coreEntityTableName.equals(entity1Value)) {
    		return new CoreToLeafDirectedNetworkQueryConstructor(
    			entity2Value,
    			GUIBuilder.HEADER_GROUP_NAME,
    			GUIBuilder.NODE_ATTRIBUTE_FUNCTION_GROUP_NAME,
    			GUIBuilder.NODE_CORE_ENTITY_COLUMN_GROUP_NAME,
    			GUIBuilder.NODE_ATTRIBUTE_NAME_GROUP_NAME,
    			GUIBuilder.EDGE_ATTRIBUTE_FUNCTION_GROUP_NAME,
    			GUIBuilder.EDGE_CORE_ENTITY_COLUMN_GROUP_NAME,
    			GUIBuilder.EDGE_ATTRIBUTE_NAME_GROUP_NAME,
    			model,
    			metadata);
    	// Leaf1 -> Leaf2
    	} else {
    		return new LeafToLeafDirectedNetworkQueryConstructor(
    			entity1Value,
    			entity2Value,
    			GUIBuilder.HEADER_GROUP_NAME,
    			GUIBuilder.NODE_ATTRIBUTE_FUNCTION_GROUP_NAME,
    			GUIBuilder.NODE_CORE_ENTITY_COLUMN_GROUP_NAME,
    			GUIBuilder.NODE_ATTRIBUTE_NAME_GROUP_NAME,
    			GUIBuilder.EDGE_ATTRIBUTE_FUNCTION_GROUP_NAME,
    			GUIBuilder.EDGE_CORE_ENTITY_COLUMN_GROUP_NAME,
    			GUIBuilder.EDGE_ATTRIBUTE_NAME_GROUP_NAME,
    			model,
    			metadata);
    	}
    	// TODO: Leaf1 -> Leaf1 (via Core) via Leaf2
    }
}