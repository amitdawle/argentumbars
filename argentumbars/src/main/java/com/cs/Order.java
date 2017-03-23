package com.cs;

class Order {
	
	private final OrderType type;
	private final String userId;
	private final double quantity;
	private final int price;

	public static enum OrderType{
		SELL, BUY
	}


	Order(OrderType type, String userId, double quantity, int price) {
		super();
		this.type = type;
		this.userId = userId;
		this.quantity = quantity;
		this.price = price;
	}
	
	public OrderType getType() {
		return type;
	}

	public String getUserId() {
		return userId;
	}

	public double getQuantity() {
		return quantity;
	}

	public int getPrice() {
		return price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + price;
		long temp;
		temp = Double.doubleToLongBits(quantity);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (price != other.price)
			return false;
		if (Double.doubleToLongBits(quantity) != Double.doubleToLongBits(other.quantity))
			return false;
		if (type != other.type)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	
	
}
