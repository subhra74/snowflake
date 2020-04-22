package muon.app.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.DirectConnection;

class SessionChannel {
	SSHClient sshClient;
	DirectConnection directConnection;
}
