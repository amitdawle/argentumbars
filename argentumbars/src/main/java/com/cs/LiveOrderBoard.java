package com.cs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;


import com.cs.Order.OrderType;
/**
*
* 'Live Order Board' that can provide us with the following functionality:
* 1) Register an order. Order must contain these fields:
* -	user id
* -	order quantity (e.g.: 3.5 kg)
* -	price per kg (e.g.: £303)
* -	order type: BUY or SELL
*
* 2) Cancel a registered order - this will remove the order from 'Live Order Board'
*
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
*
* 1. Orders for the same price should be merged together (even when they are from different users).
* 2. SELL orders the orders with lowest prices are displayed first. Opposite is true for the BUY orders. 
*
* Note: 
* A: This implementation is not thread safe
* B: Price must is specified in pennies (or cents)   
*/
public class LiveOrderBoard {
	
       /*
        * Implementation note:
        * For given list of orders
        *  o1 -> SELL 1.2kg @ 100
        *  o2 -> SELL 3.2kg @ 100
        *  o3 -> SELL 1.1kg @ 150
        *  o4 -> BUY 2 kg @ 110
        *  o5 -> BUY 3.2kg @ 110
        *  o6 -> BUY 1.1kg @ 150
        *  
	* The orders are currently stored internally as follows
	*
        *  liveOrders ---> SELL --> |100 , (o1,o2)|
        *             |             |150 , (o3)|
        *             |              
        *             |--> BUY --> | 150, (o6) |           
        *                          | 110 , (o4, o5)|
	*/                          

	private final NavigableMap<OrderType, NavigableMap<Integer, List<Order>>> liveOrders;
	
	public LiveOrderBoard(){
		liveOrders = new TreeMap<>();
		liveOrders.put(OrderType.SELL, new TreeMap<>());
		liveOrders.put(OrderType.BUY, new TreeMap<>(Comparator.reverseOrder()));
	}
	
       /**
         * @param userId Unique user id
         * @param quantity 
         * @param price in pennies/cents
         * @param type (SELL/BUY) {@link com.cs.Order.OrderType}
         * @return Created order
         * @throws NullPointerException If User Id  is null.
	 * @throws IllegalArgumentException if quantity or price is less than equal to 0.
         */
	public Order register(OrderType type, String userId, double quantity, int price){
		if(userId == null){
			throw new NullPointerException("userid must not be null");
		}
		if(quantity <= 0.0){
			throw new IllegalArgumentException("qunatity must be gerater than 0");
		}
		if(price <= 0){
			throw new IllegalArgumentException("price must be gerater than 0");
		}
		
		Order order = new Order(type, userId, quantity, price);
		liveOrders.get(type).computeIfAbsent(price, (k)->new ArrayList<>()).add(order);
		return order;
	}
	
       /**
	*
	* @return true if the order was successfully cancelled. If the order was never registered or is already 
	*         cancelled this method will return false.
	* @throws NullPointerException If the input is null. 
	*/
	public boolean cancel(Order toCancel){
		if( toCancel == null) {
			throw new NullPointerException("Order to cancel should not be null.");
		}
		
		List<Order> orders = liveOrders.get(toCancel.getType()).get(toCancel.getPrice());
		
		if(orders == null){
			return false; // nothing at this price point
		}
		
		boolean success = orders.remove(toCancel);
		
		if(orders.isEmpty()){ // no more orders at this price. Clear out so no summary is generated.
			liveOrders.get(toCancel.getType()).remove(toCancel.getPrice());
		}
		
		return success;
	}
	
	
     /**
       * The summary represents an aggregated view of all the orders of a given type and at a given price point.
       *  for e.g. given a list the following list of registered orders  
       *  o1 -> SELL 1.2kg @ 100
       *  o2 -> SELL 3.2kg @ 100
       *  o3 -> SELL 1.1kg @ 150
       *  o4 -> BUY 2 kg @ 110
       *  o5 -> BUY 3.2kg @ 110
       *  o6 -> BUY 1.1kg @ 150
       *  
       *  Summary will return
       *  (4.4, 100)     --> from o1 +o2
       *  (1.1 , 150)    --> from o3
       *  (1.1, 150)     --> o6
       *  (5.2, 110)     --> o4 + o5
       *  
       *  Note the SELL orders summary is in an ascending order (based on price) whereas BUY is in descending order (again based on price).
       *  Also this implementation returns both the SELL and Buy summary together. SELL summaries are before BUY summaries.
       * @return List of {@link OrderSummary}. 
       */
	public List<OrderSummary> summary(){
		List <OrderSummary> result = new ArrayList<>();
		
		createOrderSummary(liveOrders.get(OrderType.SELL), result);
		createOrderSummary(liveOrders.get(OrderType.BUY), result);
		
		return result;
	}
	

	private void createOrderSummary(NavigableMap<Integer, List<Order>> orders, List<OrderSummary> result) {
		
		for(Entry<Integer, List<Order>> e: orders.entrySet() ){
			int price = e.getKey();
			double quantity = e.getValue().stream().mapToDouble( o -> o.getQuantity()).sum();
			result.add(new OrderSummary(quantity, price));
		}
	}
}
