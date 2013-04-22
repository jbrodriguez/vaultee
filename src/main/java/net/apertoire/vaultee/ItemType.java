package net.apertoire.vaultee;

public class ItemType {
	private Long id;
	private String name;

	ItemType() {}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}