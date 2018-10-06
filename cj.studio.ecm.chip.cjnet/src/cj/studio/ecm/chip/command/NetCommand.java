package cj.studio.ecm.chip.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.Logger;

import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.frame.IFeedback;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;
import cj.studio.ecm.net.IClient;
import cj.ultimate.util.StringUtil;

public class NetCommand implements INetCommand {
	Logger logger = Logger.getLogger(NetCommand.class);
String prev;
	public NetCommand(String prev) {
		this.prev=prev;
	}

	@Override
	public void exe(ICommandLine line, IClient client) throws CircuitException {
		// TODO Auto-generated method stub
		
		CommandLine cl = line.line();
		String fline = line.cmd() + " " + cl.getOptionValue("u") + " "
				+ cl.getOptionValue("t");
		Frame f = new Frame(fline);
		Properties hprops = cl.getOptionProperties("H");
		if (hprops != null) {
			Enumeration<?> e = hprops.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = hprops.getProperty(key);
				f.head(key, value);
			}
		}
		Properties pprops = cl.getOptionProperties("P");
		if (pprops != null) {
			Enumeration<?> e = pprops.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = pprops.getProperty(key);
				f.parameter(key, value);
			}
		}
		// 内容
		StringBuffer sb = new StringBuffer();
		if (cl.hasOption("c")) {
			try {
				inputContent(sb);
			} catch (IOException e1) {
				System.out.println("内容输入错误："+e1.getMessage());
				return;
			}
			String content = sb.toString();
			if (!StringUtil.isEmpty(content)) {
				f.contentChartset("utf-8");
				f.head("Content-type", "text");
				try {
					f.content().writeBytes(content.getBytes("utf-8"));
				} catch (UnsupportedEncodingException e) {
					System.out.println("错误："+e.getMessage());
				}
			}
		}
		
//		Frame net=new Frame("flow / net/1.1");
//		net.head("frameWrapper","bin");
//		if(cl.hasOption("s")){
//			net.head(NetConstans.FRAME_HEADKEY_CIRCUIT_SYNC,"true");
//		}
//		net.content().writeBytes(f.toBytes());
		Circuit circuit = new Circuit(f.protocol()+" 200 ok");
		Properties cpprops = cl.getOptionProperties("C");
		if (cpprops != null) {
			Enumeration<?> e = cpprops.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = cpprops.getProperty(key);
				circuit.head(key, value);
			}
		}
		try{
			circuit.feedbackSetSource(IFeedback.KEY_INPUT_FEEDBACK);
			circuit.feedback(IFeedback.KEY_INPUT_FEEDBACK).plugSink("response", new ISink() {
				
				@Override
				public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
					System.out.println("sync answer:\r\n");
					StringBuffer sb=new StringBuffer();
					Responser.printFrame(frame, sb);
					System.out.println(sb);
					System.out.print(prev);
				}
			});
		client.buildNetGraph().netInput().flow(f, circuit);
		if("202".equals(circuit.status())){
			System.out.println(String.format("%s .可稍候等待同步回路异步返回。",circuit.message()));
		}
//		if(!"200".equals(circuit.status())||circuit.content().readableBytes()>0){
//			System.out.println("sync answer:\r\n");
//			Frame resf=circuit.snapshot();
//			sb=new StringBuffer();
//			Responser.printFrame(resf, sb);
//			System.out.println(sb);
//			System.out.print(prev);
//		}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	private void inputContent(StringBuffer sb) throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader read = new BufferedReader(input);
		System.out.println("-----------输入内容,以!q退出内容编辑------------");
		String line = "";
		// 处理侦开始，每一行均为命令行，注意lns中的各种协议nl/1.0,pc/1.0,等
		System.out.print("$:");
		while (true) {
			line = read.readLine();
			if (StringUtil.isEmpty(line))
				continue;
			
			if(line.endsWith("!q")){
//				if(line.length()>2){
//					sb.append(line.substring(0,line.lastIndexOf("!q")));
//				}else{
					sb.append(line.substring(0,line.indexOf("!q")));
//				}
				break;
			}else{
				sb.append(line);
			}
			System.out.print("$:");
		}

	}

}
