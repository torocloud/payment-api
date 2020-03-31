package io.toro.payment.api.utils.groovy

//TODO: Add compile static
class Mod10Check {

	public static boolean checkCard( String cardNumber ) {
		int index = 0
		int total = 0
		
		for(ch: cardNumber.reverse()) {
			def digit = Integer.parseInt( ch )
			total += ( index % 2 == 0 ) ? digit : [0, 2, 4, 6, 8, 1, 3, 5, 7, 9][digit]
			index++
		}

		total % 10 == 0
	}
}
