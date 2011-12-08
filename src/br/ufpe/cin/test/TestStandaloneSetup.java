
package br.ufpe.cin.test;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class TestStandaloneSetup extends TestStandaloneSetupGenerated{

	public static void doSetup() {
		new TestStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

