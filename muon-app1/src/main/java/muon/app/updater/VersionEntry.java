package muon.app.updater;

public class VersionEntry implements Comparable<VersionEntry> {
	private String tag_name;

	public VersionEntry() {
		// TODO Auto-generated constructor stub
	}

	public VersionEntry(String tag_name) {
		this.tag_name = tag_name;
	}

	@Override
	public int compareTo(VersionEntry o) {
		int v1 = getNumericValue();
		int v2 = o.getNumericValue();
		return v1 - v2;
	}

	public final int getNumericValue() {
		String arr[] = tag_name.substring(1).split("\\.");
		int value = 0;
		int multiplier = 1;
		for (int i = arr.length - 1; i >= 0; i--) {
			value += Integer.parseInt(arr[i]) * multiplier;
			multiplier *= 10;
		}
		return value;
	}

	public String getTag_name() {
		return tag_name;
	}

	public void setTag_name(String tag_name) {
		this.tag_name = tag_name;
	}

	@Override
	public String toString() {
		return "VersionEntry [tag_name=" + tag_name + " value=" + getNumericValue() + "]";
	}
}
