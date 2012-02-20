package edu.iu.sci2.visualization.geomaps.testing;

import org.cishell.framework.CIShellContext;

import edu.iu.sci2.testutilities.StdErrLogService;

/* A CIShell whose getService always returns a LogService.
 * The LogService simply writes to System.err.
 */
public class LogOnlyCIShellContext implements CIShellContext {
	public Object getService(String service) {
		return new StdErrLogService();
	}
}
