package muon.app.ssh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import muon.app.ui.components.session.PortForwardingRule;
import muon.app.ui.components.session.PortForwardingRule.PortForwardingType;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Parameters;
import net.schmizz.sshj.connection.channel.forwarded.RemotePortForwarder.Forward;
import net.schmizz.sshj.connection.channel.forwarded.SocketForwardingConnectListener;
import net.schmizz.sshj.transport.TransportException;
import muon.app.ui.components.session.SessionInfo;

public class PortForwardingSession {
	private SshClient2 ssh;
	private SessionInfo info;
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	private List<ServerSocket> ssList = new ArrayList<>();

	public PortForwardingSession(SessionInfo info, InputBlocker inputBlocker,
			CachedCredentialProvider cachedCredentialProvider) {
		this.info = info;
		this.ssh = new SshClient2(info, inputBlocker, cachedCredentialProvider);
	}

	public void close() {
		this.threadPool.submit(() -> {
			try {
				this.ssh.close();
			} catch (IOException e) {
				// TODO: handle exception
			}
			for (ServerSocket ss : ssList) {
				try {
					ss.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		this.threadPool.shutdown();
	}

	public void start() {
		this.threadPool.submit(() -> {
			this.forwardPorts();
		});
	}

	private void forwardPorts() {
		try {
			if (!ssh.isConnected()) {
				ssh.connect();
			}
			for (PortForwardingRule r : info.getPortForwardingRules()) {
				if (r.getType() == PortForwardingType.Local) {
					try {
						forwardLocalPort(r);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (r.getType() == PortForwardingType.Remote) {
					try {
						forwardRemotePort(r);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void forwardLocalPort(PortForwardingRule r) throws Exception {
		ServerSocket ss = new ServerSocket();
		ssList.add(ss);
		ss.setReuseAddress(true);
		ss.bind(new InetSocketAddress(r.getBindHost(), r.getSourcePort()));
		this.threadPool.submit(() -> {
			try {
				this.ssh.newLocalPortForwarder(
						new Parameters(r.getBindHost(), r.getSourcePort(), r.getHost(), r.getTargetPort()), ss)
						.listen();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void forwardRemotePort(PortForwardingRule r) {
		this.threadPool.submit(() -> {
			/*
			 * We make _server_ listen on port 8080, which forwards all connections to us as
			 * a channel, and we further forward all such channels to google.com:80
			 */
			try {
				ssh.getRemotePortForwarder().bind(
						// where the server should listen
						new Forward(r.getSourcePort()),
						// what we do with incoming connections that are forwarded to us
						new SocketForwardingConnectListener(new InetSocketAddress(r.getHost(), r.getTargetPort())));

				// Something to hang on to so that the forwarding stays
				ssh.getTransport().join();
			} catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
