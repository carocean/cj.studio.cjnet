package cj.studio.ecm.chip.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.ParseException;

import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.net.IClient;
import cj.ultimate.util.StringUtil;

public class NetConsole {
	public static final String COLOR_SURFACE = "\033[0;30m";
	public static final String COLOR_RESPONSE = "\033[0;34m";
	public static final String COLOR_CMDLINE = "\033[0;32m";
	public static final String COLOR_CMDPREV = "\033[0;31m";

	public void monitor(IClient client) throws IOException, ParseException,
			CircuitException {
		
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader read = new BufferedReader(input);
		String line = "";
		ICmdLineParser parser = new CmdLineParser(client.getHost() + ":"
				+ client.getPort());

		String prev = NetConsole.COLOR_CMDPREV + client.getHost() + ":"
				+ client.getPort() + " >" + NetConsole.COLOR_CMDLINE;
		INetCommand cmd = new NetCommand(prev);
		System.out.print(prev);
		// 处理侦开始，每一行均为命令行，注意lns中的各种协议nl/1.0,pc/1.0,等
		while (true) {

			line = read.readLine();
			if (StringUtil.isEmpty(line)) {
				System.out.print(prev);
				continue;
			}
			if ("exit".equals(line) || "bye".equals(line)
					|| "quit".equals(line) || "close".equals(line)) {
				break;
			}
			try {
				ICommandLine cl = parser.parse(line);
				if (cl == null)
					continue;

				cmd.exe(cl, client);
			} catch (Exception e) {
				System.out.println("命令执行错误，原因：\r\n" + e.getMessage());
				System.out.print(prev);
				continue;
			}
		}
		System.out.print(COLOR_SURFACE);
	}

}

// none = "\033[0m"
// black = "\033[0;30m"
// dark_gray = "\033[1;30m"
// blue = "\033[0;34m"
// light_blue = "\033[1;34m"
// green = "\033[0;32m"
// light_green -= "\033[1;32m"
// cyan = "\033[0;36m"
// light_cyan = "\033[1;36m"
// red = "\033[0;31m"
// light_red = "\033[1;31m"
// purple = "\033[0;35m"
// light_purple = "\033[1;35m"
// brown = "\033[0;33m"
// yellow = "\033[1;33m"
// light_gray = "\033[0;37m"
// white = "\033[1;37m"