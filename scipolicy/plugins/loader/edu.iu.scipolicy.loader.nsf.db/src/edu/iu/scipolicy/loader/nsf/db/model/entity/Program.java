package edu.iu.scipolicy.loader.nsf.db.model.entity;

import java.util.Dictionary;
import java.util.Hashtable;

import org.cishell.utilities.StringUtilities;

import edu.iu.cns.database.loader.framework.Entity;
import edu.iu.cns.database.loader.framework.utilities.DatabaseTableKeyGenerator;
import edu.iu.scipolicy.loader.nsf.db.NSFDatabase;

public class Program extends Entity<Program> {

	private String name;
	private String fundingCode;

	public Program(DatabaseTableKeyGenerator keyGenerator,
				   String name,
				   String fundingCode) {
		super(keyGenerator, createAttributes(name, fundingCode));
		this.fundingCode = fundingCode;
		this.name = name;
	}


	private static Dictionary<String, Comparable<?>> createAttributes(String name, 
														String fundingCode) {
		Dictionary<String, Comparable<?>> attributes = new Hashtable<String, Comparable<?>>();
		attributes.put(NSFDatabase.PROGRAM_NAME, name);
		attributes.put(NSFDatabase.FUNDING_CODE, fundingCode);

		return attributes;
	}

	public String getName() {
		return name;
	}

	public String getFundingCode() {
		return fundingCode;
	}
	
	@Override
	public void merge(Program otherItem) {
		this.name = StringUtilities.simpleMerge(this.name, otherItem.getName());
		this.fundingCode = StringUtilities.simpleMerge(this.fundingCode, otherItem.getFundingCode());
	}


	@Override
	public boolean shouldMerge(Program otherItem) {
		Program otherProgram = otherItem;
		boolean isCurrentProgramNameEmpty = name.length() == 0 ? true : false;
		boolean isOtherProgramNameEmpty = otherProgram.getName().length() == 0 ? true : false;

		//TODO: explain 
		if (isCurrentProgramNameEmpty && isOtherProgramNameEmpty) {
			if (fundingCode.equalsIgnoreCase(otherProgram.getFundingCode())) {
				return true;
			} else {
				return false;
			}
		} else if (!isCurrentProgramNameEmpty && !isOtherProgramNameEmpty) {
			if (name.equalsIgnoreCase(otherProgram.getName())) {
				return true;
			} else {
				return false;
			}
		} else {
			if (fundingCode.equalsIgnoreCase(otherProgram.getFundingCode())) {
				return true;
			} else {
				return false;
			}
		}
	}

}