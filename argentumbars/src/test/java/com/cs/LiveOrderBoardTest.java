package com.cs;

import static org.junit.Assert.*;

import static java.util.Collections.*;

import org.junit.Test;
import static org.hamcrest.Matchers.*;

import com.cs.Order.OrderType;

public class LiveOrderBoardTest {

	@Test
	public void register_ValidBuyOrder_OrderRegistered(){
		LiveOrderBoard board = new LiveOrderBoard();

		Order order = board.register(OrderType.BUY, "1", 1.2, 100);
		
		assertThat(order.getPrice(), is(equalTo(100)));
		assertThat(order.getUserId(), is(equalTo("1")));
		assertThat(order.getQuantity(), is(equalTo(1.2)));
		assertThat(order.getType(), is(equalTo(OrderType.BUY)));
	}
	
	@Test
	public void register_ValidSellOrder_OrderRegistered(){
		LiveOrderBoard board = new LiveOrderBoard();

		Order order = board.register(OrderType.SELL, "1", 1.2, 100);
		
		assertThat(order.getPrice(), is(equalTo(100)));
		assertThat(order.getUserId(), is(equalTo("1")));
		assertThat(order.getQuantity(), is(equalTo(1.2)));
		assertThat(order.getType(), is(equalTo(OrderType.SELL)));
	}

	@Test
	public void orderSummary_NoOrders_EmptySummary(){
		LiveOrderBoard board = new LiveOrderBoard();

		assertThat(board.summary(), is(equalTo(emptyList())));
	}
	
	@Test
	public void orderSummary_OneBuyOrder_BuyOrderSummary(){
		LiveOrderBoard board = new LiveOrderBoard();

		board.register(OrderType.BUY, "1", 1.2, 100);
		
		assertThat(board.summary(), hasSize(equalTo(1)));
		assertThat(board.summary().get(0), is(equalTo(new OrderSummary(1.2, 100))));
	}
	
	
	@Test
	public void orderSummary_OneSellOrder_SellOrderSummary(){
		LiveOrderBoard board = new LiveOrderBoard();

		board.register(OrderType.SELL, "2", 1.3, 300);
		
		assertThat(board.summary(), hasSize(equalTo(1)));
		assertThat(board.summary().get(0), is(equalTo(new OrderSummary(1.3, 300))));
	}
	
	
	@Test
	public void orderSummary_MultipleBuyOrders_QuantityAggrigatedByPriceDescendingSummary(){
		LiveOrderBoard board = new LiveOrderBoard();

		board.register(OrderType.BUY, "1", 100.0, 100);
		board.register(OrderType.BUY, "1", 350.0, 300);
		board.register(OrderType.BUY, "1", 250.0, 200);
		board.register(OrderType.BUY, "1", 100, 100);
		board.register(OrderType.BUY, "1", 50.0, 200);
		
		assertThat(board.summary(), hasSize(equalTo(3)));
		assertThat(board.summary().get(0), is(equalTo(new OrderSummary(350.0, 300))));
		assertThat(board.summary().get(1), is(equalTo(new OrderSummary(300.0, 200))));
		assertThat(board.summary().get(2), is(equalTo(new OrderSummary(200.0, 100))));
	}
	
	
	@Test
	public void orderSummary_MultipleSellOrders_QuantityAggrigatedByPriceAscendingSummary(){
		LiveOrderBoard board = new LiveOrderBoard();

		board.register(OrderType.SELL, "1", 100.0, 100);
		board.register(OrderType.SELL, "1", 350.0, 300);
		board.register(OrderType.SELL, "1", 250.0, 200);
		board.register(OrderType.SELL, "1", 100, 100);
		board.register(OrderType.SELL, "1", 50.0, 200);
		
		assertThat(board.summary(), hasSize(equalTo(3)));
		assertThat(board.summary().get(0), is(equalTo(new OrderSummary(200.0, 100))));
		assertThat(board.summary().get(1), is(equalTo(new OrderSummary(300.0, 200))));
		assertThat(board.summary().get(2), is(equalTo(new OrderSummary(350.0, 300))));
		
	}
	
	
	
	@Test
	public void orderSummary_MultipleBuySellOrders_QuantityAggrigatedByPriceSummary(){
		LiveOrderBoard board = new LiveOrderBoard();

		board.register(OrderType.SELL, "1", 100.0, 100);
		board.register(OrderType.SELL, "2", 350.0, 300);
		board.register(OrderType.SELL, "3", 250.0, 200);
		board.register(OrderType.SELL, "4", 100, 100);
		board.register(OrderType.SELL, "5", 50.0, 200);
		board.register(OrderType.BUY, "5", 1000.0, 100);
		board.register(OrderType.BUY, "4", 3500.0, 300);
		board.register(OrderType.BUY, "3", 2500.0, 200);
		board.register(OrderType.BUY, "2", 1000, 100);
		board.register(OrderType.BUY, "1", 500.0, 200);
		
		assertThat(board.summary(), hasSize(equalTo(6)));
		assertThat(board.summary().get(0), is(equalTo(new OrderSummary(200.0, 100))));
		assertThat(board.summary().get(1), is(equalTo(new OrderSummary( 300.0, 200))));
		assertThat(board.summary().get(2), is(equalTo(new OrderSummary(350.0, 300))));
		assertThat(board.summary().get(3), is(equalTo(new OrderSummary(3500.0, 300))));
		assertThat(board.summary().get(4), is(equalTo(new OrderSummary(3000.0, 200))));
		assertThat(board.summary().get(5), is(equalTo(new OrderSummary(2000.0, 100))));
	}
	
	
	@Test
	public void cancelOrder_MultipleBuySellOrders_CorrectOrderCancelled(){
		LiveOrderBoard board = new LiveOrderBoard();
		Order toCancel = board.register(OrderType.SELL, "1", 100.0, 100);
		board.register(OrderType.SELL, "2", 350.0, 300);
		board.register(OrderType.SELL, "3", 100, 100);
		board.register(OrderType.BUY, "4", 1000.0, 100);
		board.register(OrderType.BUY, "5", 3500.0, 300);
		
		boolean cancelled = board.cancel(toCancel);
		
		assertThat(cancelled, is(equalTo(true)));
		assertThat(board.summary(), hasSize(equalTo(4)));
		assertThat(board.summary().get(0), is(equalTo(new OrderSummary(100.0, 100))));
		assertThat(board.summary().get(1), is(equalTo(new OrderSummary(350.0, 300))));
		assertThat(board.summary().get(2), is(equalTo(new OrderSummary(3500.0, 300))));
		assertThat(board.summary().get(3), is(equalTo(new OrderSummary(1000.0, 100))));
		
		
	}
	
	
	
	@Test
	public void cancelOrder_OrderCancelledTwice_FalseReturned(){
		LiveOrderBoard board = new LiveOrderBoard();

		Order toCancel = board.register(OrderType.SELL, "1", 100.0, 100);
		board.register(OrderType.SELL, "2", 350.0, 300);
		board.register(OrderType.SELL, "3", 100, 100);
		board.register(OrderType.BUY, "4", 1000.0, 100);
		board.register(OrderType.BUY, "5", 3500.0, 300);
		
		boolean cancelled = board.cancel(toCancel);
		boolean cancelledAgain = board.cancel(toCancel);
		
		assertThat(cancelled, is(equalTo(true)));
		assertThat(cancelledAgain, is(equalTo(false)));
		assertThat(board.summary(), hasSize(equalTo(4)));
		assertThat(board.summary().get(0), is(equalTo(new OrderSummary(100.0, 100))));
		assertThat(board.summary().get(1), is(equalTo(new OrderSummary(350.0, 300))));
		assertThat(board.summary().get(2), is(equalTo(new OrderSummary(3500.0, 300))));
		assertThat(board.summary().get(3), is(equalTo(new OrderSummary(1000.0, 100))));
	}
	
	
	@Test
	public void cancelOrder_SingleBuyOrder_SummaryEmpty(){
		LiveOrderBoard board = new LiveOrderBoard();

		Order toCancel = board.register(OrderType.SELL, "1", 100.0, 100);
		
		boolean cancelled = board.cancel(toCancel);
		
		assertThat(cancelled, is(equalTo(true)));
		assertThat(board.summary(), hasSize(equalTo(0)));
	}
	
}
