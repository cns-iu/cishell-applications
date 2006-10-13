package edu.iu.nwb.converter.junggraphml.writer;

//Java
import java.io.File;
import java.util.Dictionary;

//OSGi
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;
//CIShell
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;

/**
 * @author Weixia(Bonnie) Huang 
 */
public class JungGraphMLFileHandler implements AlgorithmFactory {
    private MetaTypeProvider provider;

    protected void activate(ComponentContext ctxt) {
        //You may delete all references to metatype service if 
        //your algorithm does not require parameters and return
        //null in the createParameters() method
        MetaTypeService mts = (MetaTypeService)ctxt.locateService("MTS");
        provider = mts.getMetaTypeInformation(ctxt.getBundleContext().getBundle());       
    }
    protected void deactivate(ComponentContext ctxt) {
        provider = null;
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new JungGraphMLFileHandlerAlg(data, parameters, context);
    }
    public MetaTypeProvider createParameters(Data[] data) {
        return provider;
    }
    
    public class JungGraphMLFileHandlerAlg implements Algorithm {
        Data[] data;
        Dictionary parameters;
        CIShellContext context;
        LogService logger;
        
        public JungGraphMLFileHandlerAlg(Data[] data, Dictionary parameters, CIShellContext context) {
            this.data = data;
            this.parameters = parameters;
            this.context = context;
            logger = (LogService)context.getService("LogService.class.getName()");
        }

        public Data[] execute() {
        	Object inData = data[0].getData();
        	String format = data[0].getFormat();
        	if(inData instanceof File && format.equals("file:text/graphml+xml")){
        		return new Data[]{new BasicData(inData, "file-ext:xml")};          		
        	}
        	else {
        		if (!(inData instanceof File))        				
        			logger.log(LogService.LOG_ERROR, "Expect a File, but the input data is "+inData.getClass().getName());
        		else if (!format.equals("file:text/graphml+xml"))
        			logger.log(LogService.LOG_ERROR, "Expect file:text/graphml+xml, but the input format is "+format);
       			return null;
        	}

        }
    }
}