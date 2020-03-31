package gateways.braintree.wrapper

import com.braintreegateway.Result
import com.braintreegateway.Transaction
import com.braintreegateway.ValidationError
import com.braintreegateway.ValidationErrors

import io.toro.gloop.annotation.GloopObjectParameter
import io.toro.gloop.object.property.GloopObject

class BraintreeResponseMapper {

	private static BRAINTREE_OUTPUT_MODEL = 'output{\nbraintreeOutput#gateways.braintree.model.BraintreeOutput{\n}\n}'


	@GloopObjectParameter( "output{\nbraintreeOutput#gateways.braintree.model.BraintreeOutput{\n}\n}" )
	public static GloopModel mapTransactionResponse( Result<Transaction> result ) {
		GloopModel outputModel = ( GloopModel ) GloopObject.fromGloopDoc( BRAINTREE_OUTPUT_MODEL )
		GloopModel model = (GloopModel) outputModel.get( 'braintreeOutput' )

		if( result.message != null || !result.isSuccess() ) {
			if( result.message != null ) {
				model.setValue([error: result.message])
				return outputModel
			}
			ValidationErrors validationErrors = result.getErrors()
			List<ValidationError> validationErrors1 = validationErrors.allDeepValidationErrors

			model.setValue( [error: validationErrors1[0].message] )
			return outputModel
		}

		def responseMap = [
					id: result?.getTarget()?.id,
					refundId: result?.getTarget()?.refundId,
					refundedTransactionId: result?.getTarget()?.refundedTransactionId,
					authorizationExpiresAt: result?.getTarget()?.authorizationExpiresAt?.getTimeInMillis(),
					createdAt: result.getTarget()?.createdAt?.getTimeInMillis(),
					gatewayRejectionReason: result.getTarget()?.getGatewayRejectionReason(),
					processorAuthorizationCode: result?.getTarget()?.processorAuthorizationCode,
					processorResponseCode: result?.getTarget()?.processorResponseCode,
					processorResponseText: result?.getTarget()?.processorResponseText,
					processorSettlementResponseCode: result.getTarget().processorSettlementResponseCode,
					processorSettlementResponseText: result.getTarget().processorSettlementResponseText,
					status: result?.getTarget()?.getStatus(),
					partialSettlementTransactionIds: result?.getTarget()?.partialSettlementTransactionIds,
					refundIds: result?.getTarget()?.refundIds
		]

		model.setValue( responseMap )
		return outputModel
	}
}