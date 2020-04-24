package muon.app.ui.components.session;

/*
 * Port forwarding rule, meaning of host, sourcePort and targetPort changes depending on the type of port forwarding
 */
public class PortForwardingRule {

	/**
	 * @param type       Local or remote
	 * @param host       In case of local port forwarding, the destination host,
	 *                   which is accessible from remote server, in case of remote
	 *                   port forwarding this will be the network interface address
	 *                   of the service running on local system
	 * @param sourcePort Local port to bind for local port forwarding, for remote
	 *                   this is the port bind on remote server
	 * @param targetPort For local port forwarding this is the target port, for
	 *                   remote this will be the local port of that local service
	 * @param bindHost
	 */
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
