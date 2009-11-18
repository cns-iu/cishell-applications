package edu.iu.epic.visualization.linegraph.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JSplitPane;

import edu.iu.epic.visualization.linegraph.utilities.StencilData;
import edu.iu.epic.visualization.linegraph.utilities.StencilException;


/*
 * This class is the main interface used to interact with the Stencil. 
 * It maintains state that lasts longer than a single run of a Stencil
 * (e.g. which lines are visible or not visible).
 */
public class StencilController {

	private JSplitPane parent;
	private SingleRunPanel currentPanel;
	private StencilData data;

	private Map<String, Boolean> lineVisibilityStates = 
		new HashMap<String, Boolean>();
	public StencilController(JSplitPane parent, StencilData stencilData) {
		this.parent = parent;
		this.data = stencilData;
	}

	public void playFromStart() throws StencilException {
		
		//replace the old panel with a new one
		
		SingleRunPanel newPanel = createNewPanel(this.data);
		swapInNewPanel(newPanel);
		this.currentPanel = newPanel;
		
		/*
		 * tell the new panel which lines should be visible
		 * (lines visible/invisible carries over between stencil runs)
		 */
		
		Set<String> lineNames = lineVisibilityStates.keySet();
		for (String lineName : lineNames) {
			Boolean lineVisibility = lineVisibilityStates.get(lineName);
			this.currentPanel.setLineVisible(lineName, lineVisibility);
		}
		
		//start the new panel
		
		this.currentPanel.start();
	}
	
	

	public void setLineVisible(String lineName, boolean visible)
		throws StencilException {
		//TODO: put in runnable
		//make the line visible/invisible on the current panel
		this.currentPanel.setLineVisible(lineName, visible);
		//and remember its visibility/invisibility for future panels
		this.lineVisibilityStates.put(lineName, visible);
	}

	public void export() {
		this.currentPanel.export();
	}

	private void swapInNewPanel(SingleRunPanel newPanel)
			throws StencilException {

		try {
			SingleRunPanel oldPanel = this.currentPanel;
	
			this.currentPanel = newPanel;
			this.parent.setRightComponent(this.currentPanel.getComponent());
			
			if (oldPanel != null) {
				oldPanel.dispose();
			}
		} catch (Exception e) {
			throw new StencilException(e);
		}
	}

	private SingleRunPanel createNewPanel(StencilData stencilData)
			throws StencilException {
		SingleRunPanel panel = new SingleRunPanel(this.parent, stencilData);
		return panel;
	}

	
}