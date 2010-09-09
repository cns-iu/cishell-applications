package edu.iu.sci2.database.star.extract.network.guibuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cishell.utilities.ArrayListUtilities;
import org.cishell.utility.datastructure.ObjectContainer;
import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;
import org.cishell.utility.datastructure.datamodel.field.validation.FieldValidator;
import org.cishell.utility.swt.GUIBuilderUtilities;
import org.cishell.utility.swt.GUICanceledException;
import org.cishell.utility.swt.WidgetConstructionException;
import org.cishell.utility.swt.model.SWTModel;
import org.cishell.utility.swt.model.SWTModelField;
import org.cishell.utility.swt.model.datasynchronizer.DropDownDataSynchronizer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.iu.sci2.database.star.extract.common.StarDatabaseDescriptor;
import edu.iu.sci2.database.star.extract.common.guibuilder.DisplayErrorMessagesValidationAction;
import edu.iu.sci2.database.star.extract.common.guibuilder.GUIBuilder;
import edu.iu.sci2.database.star.extract.common.guibuilder.attribute.AttributeListWidget;

public class TwoLeafTableNetworkGUIBuilder extends GUIBuilder {
	private String instructionsLabelText;
	private String tutorialURL;
	private String tutorialDisplayURL;
	private int instructionsLabelHeight;
	private String sourceLeafFieldLabel;
	private String sourceLeafFieldName;
	private String targetLeafFieldLabel;
	private String targetLeafFieldName;

	public TwoLeafTableNetworkGUIBuilder(
			String instructionsLabelText,
			String tutorialURL,
			String tutorialDisplayURL,
			int instructionsLabelHeight,
			String sourceLeafFieldLabel,
			String sourceLeafFieldName,
			String targetLeafFieldLabel,
			String targetLeafFieldName) {
		this.instructionsLabelText = instructionsLabelText;
		this.tutorialURL = tutorialURL;
		this.tutorialDisplayURL = tutorialDisplayURL;
		this.instructionsLabelHeight = instructionsLabelHeight;
		this.sourceLeafFieldLabel = sourceLeafFieldLabel;
		this.sourceLeafFieldName = sourceLeafFieldName;
		this.targetLeafFieldLabel = targetLeafFieldLabel;
		this.targetLeafFieldName = targetLeafFieldName;
	}

	@Override
	@SuppressWarnings("unchecked")	// Arrays.asList creating genericly-typed arrays.
	public SWTModel createGUI(
			String windowTitle,
			int windowWidth,
			int windowHeight,
			StarDatabaseDescriptor databaseDescriptor)
			throws GUICanceledException, UniqueNameException {
		// Create validators that all of our valiated fields should know about.

		Collection<FieldValidator<String>> otherValidatorsForLeafSelectionFields =
			Arrays.<FieldValidator<String>>asList(
				this.nodeAttributesFieldValidator, this.edgeAttributesFieldValidator);
		Collection<FieldValidator<String>> otherValidatorsForNodeAttributes =
			Arrays.<FieldValidator<String>>asList(
				this.leafSelectorFieldValidator, this.edgeAttributesFieldValidator);
		Collection<FieldValidator<String>> otherValidatorsForEdgeAttributes =
			Arrays.<FieldValidator<String>>asList(
				this.leafSelectorFieldValidator, this.nodeAttributesFieldValidator);

		final String coreEntityHumanReadableName =
			databaseDescriptor.getCoreTableDescriptor().getCoreEntityHumanReadableName();
		SWTModel model = new SWTModel(SWT.NONE);

		/* Create the GUI shell, and set up its basic structure (header, node aggregates,
		 *  edge aggregates).
		 */

		Display display = GUIBuilderUtilities.createDisplay();
		Shell shell = GUIBuilderUtilities.createShell(
			display, windowTitle, windowWidth, windowHeight, 1, false);
		Composite instructionsArea = createInstructionsArea(shell);
		Group headerGroup = createHeaderGroup(shell);
		Group nodeAggregatesGroup = createAggregatesGroup(shell, NODE_ATTRIBUTES_GROUP_TEXT);
		Group edgeAggregatesGroup = createAggregatesGroup(shell, EDGE_ATTRIBUTES_GROUP_TEXT);
		Group footerGroup = createFooterGroup(shell);

		// Create the instructions/error message label.

    	StyledText instructionsLabel =
    		createInstructionsLabel(instructionsArea, instructionsLabelHeight);
    	DisplayErrorMessagesValidationAction displayErrorMessagesValidationAction =
    		new DisplayErrorMessagesValidationAction(
    			instructionsLabel,
    			this.instructionsLabelText,
    			this.tutorialURL,
    			this.tutorialDisplayURL);

		// Create the options for the Source and Target selection fields.

		List<String> allOptions =
			ArrayListUtilities.copyAndSort(databaseDescriptor.getLeafTableNames());
		allOptions = ArrayListUtilities.unionCollectionsAsList(
			Arrays.asList(coreEntityHumanReadableName),
			allOptions,
			null);
		Map<String, String> allOptionsByLabels = databaseDescriptor.getTableNameOptionsWithCore();

		// Create and setup the Source and Target selection fields.

		SWTModelField<String, Combo, DropDownDataSynchronizer<String>> sourceLeafField =
			createLeafSelectionField(
				this.sourceLeafFieldLabel,
				HEADER_GROUP_NAME,
				this.sourceLeafFieldName,
				0,
				allOptions,
				allOptionsByLabels,
				databaseDescriptor,
				model,
				headerGroup,
				otherValidatorsForLeafSelectionFields,
				displayErrorMessagesValidationAction);
		SWTModelField<String, Combo, DropDownDataSynchronizer<String>> targetLeafField =
			createLeafSelectionField(
				this.targetLeafFieldLabel,
				HEADER_GROUP_NAME,
				this.targetLeafFieldName,
				1,
				allOptions,
				allOptionsByLabels,
				databaseDescriptor,
				model,
				headerGroup,
				otherValidatorsForLeafSelectionFields,
				displayErrorMessagesValidationAction);

		/*
		 * Make it so only one of the Source and Target selection fields can be the core
		 *  entity option.
		 */

		// Create the widget that allows users to specify node and edge aggregate fields.

		AttributeListWidget nodeAggregatesWidget = createAggregateWidget(
			model,
			NODE_ATTRIBUTE_FUNCTION_GROUP_NAME,
			NODE_CORE_ENTITY_COLUMN_GROUP_NAME,
			databaseDescriptor.getCoreTableDescriptor().getColumnNames(),
			databaseDescriptor.getCoreTableDescriptor().getColumnNamesByLabels(),
			NODE_ATTRIBUTE_NAME_GROUP_NAME,
			NODE_TYPE,
			nodeAggregatesGroup,
			this.nodeAttributesFieldValidator,
			otherValidatorsForNodeAttributes,
			displayErrorMessagesValidationAction);
		AttributeListWidget edgeAggregatesWidget = createAggregateWidget(
			model,
			EDGE_ATTRIBUTE_FUNCTION_GROUP_NAME,
			EDGE_CORE_ENTITY_COLUMN_GROUP_NAME,
			databaseDescriptor.getCoreTableDescriptor().getColumnNames(),
			databaseDescriptor.getCoreTableDescriptor().getColumnNamesByLabels(),
			EDGE_ATTRIBUTE_NAME_GROUP_NAME,
			EDGE_TYPE,
			edgeAggregatesGroup,
			this.edgeAttributesFieldValidator,
			otherValidatorsForEdgeAttributes,
			displayErrorMessagesValidationAction);

		// Create the finished button so the user can actually execute the resulting queries.

		final ObjectContainer<Boolean> userFinished = new ObjectContainer<Boolean>(false);
		this.finishedButton = createFinishedButton(footerGroup, 1, userFinished);

		/* Fill the aggregate widgets with some aggregate fields by default (for the user's ease).
		 * (This has to be done after the finished button is created because the components we're
		 * about to create validate upon creation, and to validate the finished button must exist.
		 */

		try {
			for (int ii = 0; ii < DEFAULT_AGGREGATE_WIDGET_COUNT; ii++) {
				nodeAggregatesWidget.addComponent(SWT.NONE, null);
				edgeAggregatesWidget.addComponent(SWT.NONE, null);
			}
		} catch (WidgetConstructionException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		// Set the GUI up to be cancelable.

		final ObjectContainer<GUICanceledException> exceptionThrown =
			new ObjectContainer<GUICanceledException>();
		GUIBuilderUtilities.setCancelable(shell, exceptionThrown);

		// Set up validation for the leaf table selectors.

		sourceLeafField.validate();
		targetLeafField.validate();

		// Run the GUI and return the model with the data that the user entered.

		runGUI(display, shell, windowHeight);

		if ((userFinished.object == false) && (exceptionThrown.object != null)) {
    		throw new GUICanceledException(
    			exceptionThrown.object.getMessage(), exceptionThrown.object);
    	}

		return model;
	}

//	@Override
//	protected StyledText createInstructionsLabel(Composite parent, int height) {
//		StyledText instructionsLabel = super.createInstructionsLabel(parent, height);
//
//		return instructionsLabel;
//	}

	private SWTModelField<
			String, Combo, DropDownDataSynchronizer<String>> createLeafSelectionField(
				String labelText,
				String groupName,
				String fieldName,
				int selectedIndex,
				Collection<String> allOptionLabels,
				Map<String, String> allOptionValuesByLabels,
				StarDatabaseDescriptor databaseDescriptor,
				SWTModel model,
				Composite parent,
				Collection<FieldValidator<String>> otherValidators,
				DisplayErrorMessagesValidationAction displayErrorMessagesValidationAction)
				throws UniqueNameException {
		Label label = new Label(parent, SWT.READ_ONLY);
		label.setLayoutData(createLeafSelectionFieldLabelLayoutData());
		label.setText(labelText);

		SWTModelField<String, Combo, DropDownDataSynchronizer<String>> leafField =
			model.addDropDown(
				fieldName,
				"",
				groupName,
				selectedIndex,
				allOptionLabels,
				allOptionValuesByLabels,
				parent,
				SWT.BORDER | SWT.READ_ONLY);
		leafField.getWidget().setLayoutData(createLeafSelectionFieldLayoutData());
		this.leafSelectorFieldValidator.addFieldToValidate(leafField);
		leafField.addValidator(this.leafSelectorFieldValidator);
		leafField.addOtherValidators(otherValidators);
		leafField.addValidationAction(this.disableFinishedButtonAction);
		leafField.addValidationAction(displayErrorMessagesValidationAction);

		return leafField;
	}

	private static GridData createLeafSelectionFieldLabelLayoutData() {
		GridData layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);

		return layoutData;
	}

	private static GridData createLeafSelectionFieldLayoutData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, true);

		return layoutData;
	}

//	private static SelectionListener createLeafSelectionListener(
//			final List<String> allOptionsExceptCore,
//			final Map<String, String> allOptionsByLabelsExceptCore,
//			final List<String> allOptions,
//			final Map<String, String> allOptionsByLabels,
//			final String coreEntityHumanReadableName,
//			final String coreEntityTableName,
//			final SWTModelField<String, Combo, DropDownDataSynchronizer<String>> sourceLeafField,
//			final SWTModelField<String, Combo, DropDownDataSynchronizer<String>> targetLeafField) {
//		return new SelectionListener() {
//			public void widgetDefaultSelected(SelectionEvent event) {
//				selected(event);
//			}
//
//			public void widgetSelected(SelectionEvent event) {
//				selected(event);
//			}
//
//			private void selected(SelectionEvent event) {
//				if (coreEntityTableName.equals(sourceLeafField.getValue())) {
//					final String targetLeafFieldValue = targetLeafField.getValue();
//					targetLeafField.getDataSynchronizer().setOptions(
//						allOptionsExceptCore, allOptionsByLabelsExceptCore);
//					targetLeafField.setValue(targetLeafFieldValue);
//				} else if (coreEntityTableName.equals(sourceLeafField.getPreviousValue())) {
//					final String targetLeafFieldValue = targetLeafField.getValue();
//					targetLeafField.getDataSynchronizer().setOptions(
//						allOptions, allOptionsByLabels);
//					targetLeafField.setValue(targetLeafFieldValue);
//				}
//			}
//		};
//	}

//	private class LeafTableSelectionValidator implements FieldValidationRule<String> {
//		private SWTModelField<String, Combo, DropDownDataSynchronizer<String>> sourceLeafField;
//		private SWTModelField<String, Combo, DropDownDataSynchronizer<String>> targetLeafField;
//		private List<DataModelField<?>> alsoValidateFields =
//			new ArrayList<DataModelField<?>>(1);
//
//		public LeafTableSelectionValidator(
//				SWTModelField<String, Combo, DropDownDataSynchronizer<String>> sourceLeafField,
//				SWTModelField<String, Combo, DropDownDataSynchronizer<String>> targetLeafField) {
//			this.sourceLeafField = sourceLeafField;
//			this.targetLeafField = targetLeafField;
//		}
//
//		public void validateField(DataModelField<String> field, DataModel model)
//				throws ModelValidationException {
//			if (this.sourceLeafField.getValue().equals(this.targetLeafField.getValue())) {
////				if (field == this.sourceLeafField) {
////					this.alsoValidateFields.set(0, this.targetLeafField)
////				}
//
//				String format =
//					"The Source and Target leaf tables must differ for this type of network " +
//					"extraction.  They both have the '%s' table selected.";
//				String exceptionMessage = String.format(format, this.sourceLeafField.getValue());
//				throw new ModelValidationException(exceptionMessage);
//			}
//		}
//
//		public void fieldDisposed(DataModelField<String> field) {
//		}
//	}
}