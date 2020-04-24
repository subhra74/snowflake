package muon.app.ui.components.session;

public class PortForwardingRule {
	public PortForwardingRule(PortForwardingType type, String host, int sourcePort, int targetPort, String bindHost) {
		super();
		this.type = type;
		this.host = host;
		this.sourcePort = sourcePort;
		this.targetPort = targetPort;
		this.bindHost = bindHost;
	}

	public PortForwardingRule() {
	}

	public enum PortForwardingType {
		Local, Remote
	}

	private PortForwardingType type;
	private String host, bindHost;
	private int sourcePort, targetPort;

	public PortForwardingType getType() {
		return type;
	}

	public void setType(PortForwardingType type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}

	public int getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(int targetPort) {
		this.targetPort = targetPort;
	}

	public String getBindHost() {
		return bindHost;
	}

	public void setBindHost(String bindHost) {
		this.bindHost = bindHost;
	}
}
