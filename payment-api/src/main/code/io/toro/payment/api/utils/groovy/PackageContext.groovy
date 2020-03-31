package io.toro.payment.api.utils.groovy

import io.toro.gloop.annotation.GloopComment
import io.toro.martini.GroovyMethods
import io.toro.martini.ipackage.MartiniPackage

class PackageContext {

	@GloopComment( 'Sets an Integrate package to the current context' )
	public static setPackageContext(String packageName) {
		Optional<MartiniPackage> newPackage = GroovyMethods.martiniPackageProvider.getPackage(packageName)
		if( newPackage.isPresent() )
			GroovyMethods.MartiniPackageSetter.setPackage( newPackage.get() )
	}
}
