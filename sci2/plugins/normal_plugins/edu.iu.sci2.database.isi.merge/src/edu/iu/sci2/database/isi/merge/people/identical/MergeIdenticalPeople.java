package edu.iu.sci2.database.isi.merge.people.identical;

import java.sql.SQLException;
import java.util.Collection;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.service.database.Database;
import org.cishell.utilities.DataFactory;
import org.osgi.service.log.LogService;

import prefuse.data.Table;
import edu.iu.cns.database.merge.generic.detect.redundant_author.MergeGroup;
import edu.iu.cns.database.merge.generic.detect.redundant_author.MergeGroup.DuplicateAuthorException;
import edu.iu.cns.database.merge.generic.detect.redundant_author.MergeGroupSettings;
import edu.iu.cns.database.merge.generic.prepare.marked.MergeMarker;
import edu.iu.cns.database.merge.generic.prepare.marked.grouping.KeyBasedGroupingStrategy;
import edu.iu.sci2.database.isi.merge.people.IsiPersonPriorities;


public class MergeIdenticalPeople implements Algorithm, ProgressTrackable {
	public static final MergeGroupSettings MERGE_GROUP_SETTINGS;
	static {
		
		final String MERGE_GROUP_PK_IDENTIFIER = "PK";
		final String MERGE_GROUP_IDENTIFIER = "Merge Group Identifier";
		
		final String AUTHORS_TABLE = "APP.AUTHORS";
		final String AUTHORS_DOCUMENT_FK = AUTHORS_TABLE + ".AUTHORS_DOCUMENT_FK";
		final String AUTHORS_PERSON_FK = AUTHORS_TABLE + ".AUTHORS_PERSON_FK";
		
		final String PERSON_TABLE = "APP.PERSON";
		final String PERSON_ID = PERSON_TABLE + ".UNSPLIT_NAME";
		final String PERSON_PK = PERSON_TABLE + ".PK";
		
		final String DOCUMENT_TABLE = "APP.DOCUMENT";
		final String DOCUMENT_ID = DOCUMENT_TABLE + ".ISI_UNIQUE_ARTICLE_IDENTIFIER";
		final String DOCUMENT_PK = DOCUMENT_TABLE + ".PK";
		
		MERGE_GROUP_SETTINGS = new MergeGroupSettings(
				MERGE_GROUP_PK_IDENTIFIER, MERGE_GROUP_IDENTIFIER,
				AUTHORS_TABLE, AUTHORS_DOCUMENT_FK, AUTHORS_PERSON_FK,
				PERSON_TABLE, PERSON_ID, PERSON_PK, DOCUMENT_TABLE,
				DOCUMENT_ID, DOCUMENT_PK);
	}
	
	private Data[] data;
	private CIShellContext context;
	private LogService logger;
	private ProgressMonitor monitor = ProgressMonitor.NULL_MONITOR;
	
	public MergeIdenticalPeople(Data[] data, CIShellContext context,
			LogService logger) {
		this.data = data;
		this.context = context;
		this.logger = logger;
	}

	public Data[] execute() throws AlgorithmExecutionException {
		MergeMarker mergeMarker = new MergeMarker(
				new KeyBasedGroupingStrategy<String>(
						new IsiSimpleNameNormalized()),
				new IsiPersonPriorities());

		Database database = (Database) data[0].getData();

		Table mergeTable = mergeMarker.createMarkedMergingTable(MERGE_GROUP_SETTINGS.PERSON_TABLE,
				database, context);

		try {
			Collection<MergeGroup> mergeGroups = MergeGroup.createMergeGroups(
					mergeTable, database, MERGE_GROUP_SETTINGS);

			MergeGroup.checkForAuthorDuplication(mergeGroups);
		} catch (DuplicateAuthorException e) {
			logger.log(LogService.LOG_WARNING, e.getMessage());
		} catch (SQLException e) {
			throw new AlgorithmExecutionException(
					"A SQL Exception occured when trying to check ISI "
							+ "Merge Identical People for errors:\n"
							+ e.getMessage());
		}
		
		Database merged = MergeMarker.executeMerge(mergeTable, database, context, monitor);
		
		Data mergedData = DataFactory.likeParent(merged, data[0],
				"with identical people merged");
		
		Data mergedTable = DataFactory.withClassNameAsFormat(mergeTable,
				DataProperty.TABLE_TYPE, data[0], "Merge Table: based on "
						+ MERGE_GROUP_SETTINGS.PERSON_TABLE);
		
		return new Data[] { mergedData, mergedTable };
	}


	public ProgressMonitor getProgressMonitor() {
		return monitor;
	}

	public void setProgressMonitor(ProgressMonitor monitor) {
		this.monitor = monitor;
	}
}