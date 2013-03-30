package net.apertoire.vaultee;

import org.joda.time.DateTime;

public class Revision {
	private Long id;
	private Asset asset;
	private Long index;
	private DateTime created;

	Revision() {}

	public Long getId() {
		return id;
	}

	public Asset getAsset() {
		return asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}
	
	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public DateTime getCreated() {
		return created;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != getClass()) {
			return false;
		}

		return id.equals(((Revision) other).id);
	}
}