package net.apertoire.vaultee;

import java.math.BigDecimal;

public class Product {
	private Long id;
	private ItemType itemType;
	private String name;
	private String asin;
	private String sku;
	private String upc;
	private String ean;

	Product() {}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}	

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}
}

// public class Item {
// 	private Long id;
// 	private Revision revision;
// 	private Product product;
// 	private Long quantity;
// 	private BigDecimal price;

// 	Item() {}

// 	public Long getId() {
// 		return id;
// 	}

// 	public Revision getRevision() {
// 		return revision;
// 	}

// 	public void setRevision(Revision revision) {
// 		this.revision = revision;
// 	}
	
// 	public Product getProduct() {
// 		return product;
// 	}

// 	public void setProduct(Product product) {
// 		this.product = product;
// 	}

// 	public Long getQuantity() {
// 		return quantity;
// 	}

// 	public void setQuantity(Long quantity) {
// 		this.quantity = quantity;
// 	}

// 	public BigDecimal getPrice() {
// 		return price;
// 	}

// 	public void setPrice(BigDecimal price) {
// 		this.price = price;
// 	}

// 	@Override
// 	public int hashCode() {
// 		return id.hashCode();
// 	}

// 	@Override
// 	public boolean equals(Object other) {
// 		if (other == null || other.getClass() != getClass()) {
// 			return false;
// 		}

// 		return id.equals(((Item) other).id);
// 	}
// }