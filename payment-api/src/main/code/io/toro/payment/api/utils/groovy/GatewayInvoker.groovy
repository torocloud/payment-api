package io.toro.payment.api.utils.groovy

import io.toro.gloop.Gloop;
import io.toro.gloop.engine.GloopExecutionContext;
import io.toro.gloop.object.property.GloopModel
import io.toro.martini.GroovyMethods
import io.toro.martini.ipackage.MartiniPackage
import io.toro.martini.coder.development.impl.gloop.PackageAwareGloopExecutionContextConfigurer

class GatewayInvoker {

	public static invokeGateway( String packageName, String namespace, GloopModel paymentRequest ) {

		Optional<MartiniPackage> martiniPackage = GroovyMethods.martiniPackageProvider.getPackage( packageName )
		GloopExecutionContext gloopExecutionContext = new GloopExecutionContext()
		PackageAwareGloopExecutionContextConfigurer.INSTANCE.accept( martiniPackage.get(), gloopExecutionContext )
		
		return Gloop.invokeGloop( gloopExecutionContext, namespace, paymentRequest )
	}
}