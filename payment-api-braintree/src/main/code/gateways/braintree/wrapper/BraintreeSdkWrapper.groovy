package gateways.braintree.wrapper

import com.braintreegateway.BraintreeGateway
import com.braintreegateway.Environment

import com.braintreegateway.TransactionRequest
import com.braintreegateway.Result
import com.braintreegateway.Transaction
import com.braintreegateway.ValidationError
import com.braintreegateway.ValidationErrors

import gateways.braintree.wrapper.BraintreeResponseMapper

import io.toro.gloop.annotation.GloopObjectParameter
import io.toro.gloop.object.property.GloopModel
import io.toro.gloop.object.property.GloopObject


class BraintreeSdkWrapper {
	
	public static BraintreeGateway setup() {
		return new BraintreeGateway(
		  Environment."${'braintree.environment'.getPackageProperty( 'SANDBOX' )}",
		  'braintree.merchantId'.getPackageProperty(),
		  'braintree.publicKey'.getPackageProperty(),
		  'braintree.privateKey'.getPackageProperty()
		)
	}
	
	@GloopObjectParameter( "output{\nbraintreeOutput#gateways.braintree.model.BraintreeOutput{\n}\n}" )
	public static GloopModel Authorise( @GloopObjectParameter(
		"authorizeInput#gateways.braintree.model.AuthorizeInput{\n}" ) GloopModel inputModel,
	    String amount, String trackerId ) {

		TransactionRequest request = new TransactionRequest()
			.amount( new BigDecimal( amount ) )
			.creditCard()
			    .cardholderName( inputModel?.creditCard?.cardHolderName )
				.number( inputModel?.creditCard?.number )
				.expirationMonth( inputModel?.creditCard?.expirationMonth )
				.expirationYear( inputModel?.creditCard?.expirationYear )
			    .done()
			.billingAddress()
			    .firstName( inputModel?.billingAddress?.firstName )
				.lastName( inputModel?.billingAddress?.lastName )
				.company( inputModel?.billingAddress?.company )
				.streetAddress( inputModel?.billingAddress?.streetAddress )
				.extendedAddress( inputModel?.billingAddress?.extendedAddress )
				.locality( inputModel?.billingAddress?.locality )
				.region( inputModel?.billingAddress?.region )
				.postalCode( inputModel?.billingAddress?.postalCode )
				.countryCodeAlpha2( inputModel?.billingAddress?.countryCodeAlpha2 )
				.done()

		def maskedCc = 'io.toro.payment.api.utils.gloop.MaskCreditCardNumber'.gloop( inputModel?.creditCard?.number )
		inputModel.creditCard.number = maskedCc?.maskedCCNumber
		inputModel.creditCard.cvv = '***'

		trackerId.addDocumentState( 'Sent to gateway' , inputModel.asJson( false, false ) )
			
		Result<Transaction> result = setup().transaction().sale(request)

		return BraintreeResponseMapper.mapTransactionResponse( result )
	}
	
	@GloopObjectParameter( "output{\nbraintreeOutput#gateways.braintree.model.BraintreeOutput{\n}\n}" )
	public static GloopModel Capture( String amount, String serverTransactionReference, String trackerId ) {

		logRequestToTracker( amount, serverTransactionReference, trackerId )

		Result<Transaction> result = setup().transaction().submitForSettlement(
			serverTransactionReference, 
			new BigDecimal( amount )
		)

		return BraintreeResponseMapper.mapTransactionResponse( result )
	}
	
	@GloopObjectParameter( "output{\nbraintreeOutput#gateways.braintree.model.BraintreeOutput{\n}\n}" )
	public static GloopModel Refund( String amount, String serverTransactionReference, String trackerId ) {

		logRequestToTracker( amount, serverTransactionReference, trackerId )

		Result<Transaction> result = setup().transaction().refund(
			serverTransactionReference,
			new BigDecimal( amount )
		)

		return BraintreeResponseMapper.mapTransactionResponse( result )
	}
	
	@GloopObjectParameter( "output{\nbraintreeOutput#gateways.braintree.model.BraintreeOutput{\n}\n}" )
	public static GloopModel Cancel( String serverTransactionReference, String trackerId ) {

		logRequestToTracker( null, serverTransactionReference, trackerId )

		Result<Transaction> result = setup().transaction().voidTransaction(
			serverTransactionReference,
		)

		return BraintreeResponseMapper.mapTransactionResponse( result )
	}

	private static void logRequestToTracker( String amount, String refId, String trackerId ) {

		GloopModel payload = new GloopModel()

		payload?.setAllowExtraProperties( true )
		payload?.setValue( [amount: amount, transactionId: refId] )

		trackerId.addDocumentState( 'Sent to gateway', payload.asJson( false, false ) )
	}

}