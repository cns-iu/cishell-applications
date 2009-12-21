package edu.iu.cns.database.loader.framework;

import java.util.Dictionary;

import edu.iu.cns.shared.utilities.DictionaryIterator;
import edu.iu.cns.shared.utilities.Pair;

public abstract class RowItem<T extends RowItem<?>> {
	public static final int BASE_HASH_MULTIPLIER = 31;

	private Dictionary<String, Comparable<?>> attributes;

	public RowItem(Dictionary<String, Comparable<?>> attributes) {
		this.attributes = attributes;
	}

	public final Dictionary<String, Comparable<?>> getAttributes() {
		return this.attributes;
	}

	/**
	 * This is meant to be overridden.
	 * By default, it will do a naive equality check between all of the attributes of this
	 *  and otherEntity.
	 */
	/*public boolean shouldMerge(T otherItem) {
		if (this == otherItem) {
			return true;
		}

		Dictionary<String, Comparable<?>> attributes = getAttributes();
		Dictionary<String, Comparable<?>> otherAttributes = otherItem.getAttributes();

		if (attributes.size() != otherAttributes.size()) {
			return false;
		}

		for (Pair<String, Comparable<?>> dictionaryEntry :
				new DictionaryIterator<String, Comparable<?>>(attributes)) {
			Object otherValue = otherAttributes.get(dictionaryEntry.getFirstObject());

			if (!dictionaryEntry.getSecondObject().equals(otherValue)) {
				return false;
			}
		}

		return true;
	}*/
	public abstract boolean shouldMerge(T otherItem);

	/**
	 * merge assumes that shouldMerge(otherAddress) would return true.
	 */
	public abstract void merge(T otherItem);

	public Dictionary<String, Comparable<?>> getAttributesForQuery() {
		return this.attributes;
	}

	public String toQuery() {
		// TODO: Implement this.
		return null;
	}
}