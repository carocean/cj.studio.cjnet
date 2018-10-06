package cj.studio.ecm.chip.command;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import cj.studio.ecm.EcmException;
import cj.studio.ecm.annotation.CjService;
import cj.studio.ecm.annotation.CjServiceRef;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.net.IClient;
import cj.studio.ecm.net.IConnectCallback;
import cj.studio.ecm.net.graph.INetGraph;

@CjService(name = "main",isExoteric=true)
public class Main {
	Logger logger = Logger.getLogger(Main.class);
	@CjServiceRef
	IClient udt;
	@CjServiceRef
	IClient rioUdt;
	@CjServiceRef
	IClient rioTcp;
	@CjServiceRef
	IClient tcp;
	@CjServiceRef
	IClient http;
	@CjServiceRef
	IClient websocket;
	@CjServiceRef
	IClient udp;
	@CjServiceRef
	IClient local;
	@CjServiceRef
	IClient jdkHttp;
	public void main(CommandLine cmdline) throws IOException, ParseException, CircuitException {
		String protocol = cmdline.getOptionValue("t");
		String host = cmdline.getOptionValue("h");
		String arr[] = host.split(":");
		IClient client = null;
		try {
			switch (protocol) {
			case "udt":
				if (udt == null)
					throw new EcmException("暂不支持udt协议");
				client = udt;
				break;
			case "rio-udt":
				if (udt == null)
					throw new EcmException("暂不支持rio-udt协议");
				client = rioUdt;
				break;
			case "rio-http":
				if (jdkHttp == null)
					throw new EcmException("暂不支持rio-http协议");
				client = jdkHttp;
				break;
			case "rio-tcp":
				if (rioTcp == null)
					throw new EcmException("暂不支持rio-tcp协议");
				client = rioTcp;
				break;	
			case "tcp":
				if (tcp == null)
					throw new EcmException("暂不支持tcp协议");
				client = tcp;
				break;
			case "udp":
				if (udp == null)
					throw new EcmException("暂不支持udp协议");
				client = udp;
				break;
			case "http":
				if (http == null)
					throw new EcmException("暂不支持http协议");
				client = http;
				break;
			case "websocket":
				if (websocket == null)
					throw new EcmException("暂不支持websocket协议");
				client = websocket;
				break;
			case "local":
				if (local == null)
					throw new EcmException("暂不支持local协议");
				client = local;
				break;
			}
			if(client!=null){
				if(cmdline.hasOption("l"))
					client.setProperty("log", "true");
				
				client.connect(arr[0], arr[1],new IConnectCallback() {
					
					@Override
					public void buildGraph(Object owner,INetGraph ng) {
						INetGraph g = ng;
						if (g.netOutput() != null) {
							g.netOutput().plugLast("consoleReceive",
									new Responser(g.options("host") + ":" +g.options("port")));
						}
					}
				});
			}
//			logger.info("连接成功.");
		} catch (Exception e) {
			logger.error(e);
			if (client != null) {
				client.close();
				// client.getNetGraph().dispose();
			}
//			System.exit(0);
			return;
		}
		NetConsole console=new NetConsole();
		console.monitor(client);
		
		if (client != null) {
			client.close();
			// client.getNetGraph().dispose();
		}
		try {//如果3秒后还没退出，则强制
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			
		}finally{
			System.exit(0);
		}
		return;
	}
	
}
