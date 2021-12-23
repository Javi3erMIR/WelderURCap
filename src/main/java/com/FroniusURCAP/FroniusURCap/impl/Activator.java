package com.FroniusURCAP.FroniusURCap.impl;

import WeldProgramNodes.WeldOFFService;
import WeldProgramNodes.WeldONService;
import FroniusInstallationNode.FroniusInsService;
import TrackingNodes.TrackingService;
import com.ur.urcap.api.contribution.InstallationNodeService;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;

/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("Activator says Hello World!");
		//bundleContext.registerService(SwingInstallationNodeService.class,new FroniusSetupService(), null);
		bundleContext.registerService(SwingInstallationNodeService.class, new FroniusInsService(), null);
		bundleContext.registerService(SwingProgramNodeService.class, new WeldOFFService(), null);
		bundleContext.registerService(SwingProgramNodeService.class,new WeldONService(), null);
		bundleContext.registerService(SwingProgramNodeService.class, new TrackingService(), null);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Activator says Goodbye World!");
	}
}

