package models;

public class ProductStock {

	private Product product;
	private int stock;
	
	public ProductStock(Product product, int stock){
		this.product = product;
		this.stock = stock;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}
	
}
