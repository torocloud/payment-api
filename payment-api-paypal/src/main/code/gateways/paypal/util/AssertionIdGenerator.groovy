package gateways.paypal.util

import org.apache.commons.codec.binary.Base64

import io.toro.gloop.annotation.GloopParameter

class AssertionIdGenerator {


	@GloopParameter( name = "assertId", value = 'The assertion id to be sent in the request header' )
	public static String generateAssertId( String algPayload, String payload ) {

		byte[] encodedAlgBytes = Base64.encodeBase64( algPayload.bytes )
		byte[] encodedPayloadBytes = Base64.encodeBase64( payload.bytes )
		
		return "${new String( encodedAlgBytes )}.${new String( encodedPayloadBytes )}."
	}
}
