# argentumbars

* 'Live Order Board' that can provide us with the following functionality:
* 1) Register an order. Order must contain these fields:
* -	user id
* -	order quantity (e.g.: 3.5 kg)
* -	price per kg (e.g.: £303)
* -	order type: BUY or SELL

* 2) Cancel a registered order - this will remove the order from 'Live Order Board'

* 3) Get summary information of live orders (see explanation below)

* Imagine we have received the following orders:
* -	a) SELL: 3.5 kg for £306 [user1]
* -	b) SELL: 1.2 kg for £310 [user2]
* -	c) SELL: 1.5 kg for £307 [user3]
* -	d) SELL: 2.0 kg for £306 [user4]

* ‘Live Order Board’ should provide us the following summary information:
* -	5.5 kg for £306 // order a + order d
* -	1.5 kg for £307 // order c
* -	1.2 kg for £310 // order b


* 1. Orders for the same price should be merged together (even when they are from different users).
* 2. SELL orders the orders with lowest prices are displayed first. Opposite is true for the BUY orders. 

