package edu.iu.epic.simulator.runner.utility.preprocessing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.cishell.utilities.FileUtilities;

import edu.iu.epic.simulator.runner.EpidemicSimulatorAlgorithm;

public class InitialFileMaker {
	public static final String FILENAME = "initial";
	public static final String FILE_EXTENSION = "txt";
	
	private Map<String, Float> initialDistribution;
	
	private static StringTemplateGroup initialFileTemplateGroup =
		EpidemicSimulatorAlgorithm.loadTemplates(
				"/edu/iu/epic/simulator/runner/utility/preprocessing/initialFile.st");
	
	public InitialFileMaker(Map<String, Float> initialDistribution) {
		this.initialDistribution = initialDistribution;
	}
	
	public File make() throws IOException {		
		StringTemplate template = initialFileTemplateGroup.getInstanceOf("initialFile");
		
		for (Entry<String, Float> compartmentFraction : initialDistribution.entrySet()) {
			String compartmentName = compartmentFraction.getKey();
			Float fraction = compartmentFraction.getValue();
			
			template.setAttribute("compartmentPopulations",
					new CompartmentPopulationFormatter(compartmentName, fraction));
		}
		
		
		File file = FileUtilities.createTemporaryFileInDefaultTemporaryDirectory(
					FILENAME, FILE_EXTENSION);
		
		FileWriter writer = new FileWriter(file);
		writer.write(template.toString());
		writer.close();
		
		return file;
	}
	
	private static class CompartmentPopulationFormatter {
		private String compartmentName;
		private Object population;
		
		public CompartmentPopulationFormatter(
				String compartmentName, Object population) {
			this.compartmentName = compartmentName;
			this.population = population;
		}
		
		@Override
		public String toString() {
			return this.compartmentName + " " + this.population;
		}
	}
}
