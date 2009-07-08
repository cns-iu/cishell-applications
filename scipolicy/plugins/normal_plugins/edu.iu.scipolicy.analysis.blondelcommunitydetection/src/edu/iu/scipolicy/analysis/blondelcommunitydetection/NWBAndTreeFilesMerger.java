package edu.iu.scipolicy.analysis.blondelcommunitydetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import edu.iu.nwb.util.nwbfile.NWBFileProperty;
import edu.iu.nwb.util.nwbfile.NWBFileWriter;
import edu.iu.scipolicy.utilities.SetUtilities;

public class NWBAndTreeFilesMerger extends NWBFileWriter  {
	public static final String BASE_COMMUNITY_LEVEL_ATTRIBUTE_NAME =
		"blondel_community_level_";
	public static final String BASE_COMMUNITY_LABEL = "community_";
	public static final String BASE_ISOLATE_LABEL = "isolate_";
	
	private ArrayList nodes;
	private int isolateCount = 0;
	
	public NWBAndTreeFilesMerger(File treeFile,
								 File outputFile,
								 ArrayList nodes)
			throws FileNotFoundException,
				   IOException,
				   TreeFileParsingException {
		super(outputFile);
		
		this.nodes = nodes;
		
		this.readTreeFileAndAnnotateNodes(new Scanner(treeFile));
	}
	
	public void addNode(int id, String label, Map attributes) {
		Node node = Node.findNodeByOriginalID(id);
		Map annotatedAttributes = new HashMap();
		annotatedAttributes.putAll(attributes);
		
		if (node != null) {
			ArrayList nodeCommunities = node.getCommunities();
			
			for (int ii = 0; ii < nodeCommunities.size(); ii++) {
				String communityLevelAttributeName =
					BASE_COMMUNITY_LEVEL_ATTRIBUTE_NAME + ii;
				String communityName =
					BASE_COMMUNITY_LABEL + nodeCommunities.get(ii);
				annotatedAttributes.put(communityLevelAttributeName,
										communityName);
			}
		}
		// Isolate nodes would not been added to our nodes list.
		else {
			for (int ii = 0; ii < Node.getMaxCommunityLevel(); ii++) {
				String communityLevelAttributeName =
					BASE_COMMUNITY_LEVEL_ATTRIBUTE_NAME + ii;
				String communityName = BASE_ISOLATE_LABEL + this.isolateCount;
				annotatedAttributes.put(communityLevelAttributeName,
										communityName);
				
				this.isolateCount++;
			}
		}
		
		super.addNode(id, label, annotatedAttributes);
	}
	
	public void setNodeSchema(LinkedHashMap schema) {
		for (int ii = 0; ii < Node.getMaxCommunityLevel(); ii++) {
			String communityLevelAttributeName =
				BASE_COMMUNITY_LEVEL_ATTRIBUTE_NAME + ii;
			schema.put(communityLevelAttributeName,
					   NWBFileProperty.TYPE_STRING);
		}
		
		super.setNodeSchema(schema);
	}
	
	private void readTreeFileAndAnnotateNodes(Scanner treeFileScanner)
			throws TreeFileParsingException {
		Map previousMap = null;
		Map currentMap = null;
		
		boolean shouldKeepReading = true;
		while (shouldKeepReading) {
			if (!this.checkForAnotherEntry(treeFileScanner)) {
				shouldKeepReading = false;
			}
			else {
				Integer nodeID = this.readNextNodeID(treeFileScanner);
				Integer communityID =
					this.readNextCommunityID(treeFileScanner);
				
				if (nodeID.intValue() == 0) {
					previousMap = currentMap;
					currentMap = new HashMap();
				}
				
				if (previousMap != null) {
					// nodeID is a community from the previous level.
					ArrayList nodesInCommunity =
						SetUtilities.getKeysOfMapEntrySetWithValue(
							previousMap.entrySet(), nodeID);
					
					for (int ii = 0; ii < nodesInCommunity.size(); ii++) {
						Integer currentNodeID =
							(Integer)nodesInCommunity.get(ii);
						currentMap.put(currentNodeID, communityID);
						Node node = (Node)this.nodes.get(currentNodeID.intValue());
						node.addCommunity(communityID);
					}
				}
				else {
					currentMap.put(nodeID, communityID);
					Node node = (Node)this.nodes.get(nodeID.intValue());
					node.addCommunity(communityID);
				}
			}
		}
	}
	
	private boolean checkForAnotherEntry(Scanner treeFileScanner)
			throws TreeFileParsingException {
		if (treeFileScanner.hasNext()) {
			if (!treeFileScanner.hasNextInt()) {
				throw new TreeFileParsingException(
					"A non-integer was found.  " +
					"Tree files must contain only pairs of integers.");
			}
			else {
				return true;
			}
		}
		else {
			return false;
		}
	}
	
	private Integer readNextNodeID(Scanner treeFileScanner)
			throws TreeFileParsingException {
		return new Integer(treeFileScanner.nextInt());
	}
	
	private Integer readNextCommunityID(Scanner treeFileScanner)
			throws TreeFileParsingException {
		if (!treeFileScanner.hasNext() ||
				!treeFileScanner.hasNextInt()) {
			throw new TreeFileParsingException(
				"A single integer was found.  " +
				"Tree files must contain only pairs of integers.");
		}
		else {
			return new Integer(treeFileScanner.nextInt());
		}
	}
}