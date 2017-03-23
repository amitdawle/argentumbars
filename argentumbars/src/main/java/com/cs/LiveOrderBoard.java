package com.cs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;


import com.cs.Order.OrderType;

public class LiveOrderBoard {
	
	private final NavigableMap<OrderType, NavigableMap<Integer, List<Order>>> liveOrders;
	
	public LiveOrderBoard(){
		liveOrders = new TreeMap<>();
		liveOrders.put(OrderType.SELL, new TreeMap<>());
		liveOrders.put(OrderType.BUY, new TreeMap<>(Comparator.reverseOrder()));
	}
	

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


	int orderCount(OrderType type){
		int count = 0;
		Map<Integer, List<Order>> orders = liveOrders.get(type);
		for(List<Order> o : orders.values()){
			count += o.size();
		}
		return count;
		
	}
	
}
