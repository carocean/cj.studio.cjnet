package cj.studio.ecm.cjnet;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cj.studio.ecm.Assembly;
import cj.studio.ecm.IAssembly;
import cj.studio.ecm.adapter.IActuator;
import cj.studio.ecm.adapter.IAdaptable;
import cj.ultimate.util.StringUtil;

public class Main {
	private static String fileName;

	// java -jar cjnet -h 127.0.0.1:10000 -t udt -debug cmdassembly.jar
	@SuppressWarnings("static-access")
	public static void main(String... args) throws IOException, ParseException {
		fileName = "cj.studio.ecm.chip.cjnet";
		Options options = new Options();
		Option h = new Option("h", "host",true, "必须指定远程地址，格式：-h ip:port");
		options.addOption(h);
		Option t = new Option("t","tranprotocol", true, "指定传输协议，支持：\r\nudt,tcp,http,websocket,udp,local");
		options.addOption(t);
		Option  l = new Option("l","log", false, "充许网络日志输出到控制台");
		options.addOption(l);
		Option  m = new Option("m","man", false, "帮助");
		options.addOption(m);
		Option debug = new Option("d","debug", true, "调试命令行程序集时使用，需指定以下jar包所在目录\r\n"+fileName);
		options.addOption(debug);
		
		Option thread  = OptionBuilder.withArgName( "poolPropName=value" )
                .hasArgs(2)
                .withValueSeparator()
                .withDescription( "设置客户端连接池属性， \r\n工作线程数-Twork=n,池最大大小-Tmax=n,\r\n池空间数-Tidle=n,\r\n池最小数-Tmin=n,\r\n超时时间-Ttimeout=n" )
                .create( "T" );
		options.addOption(thread);
		Option p  = OptionBuilder.withArgName( "property=value" )
                .hasArgs(2)
                .withValueSeparator()
                .withDescription( "设置客户端连接属性,格式为：-Pproperty=value" )
                .create( "P" );
		options.addOption(p);
//		GnuParser
//		BasicParser
//		PosixParser
		GnuParser parser = new GnuParser();
		CommandLine line = parser.parse(options, args);
		if(line.hasOption("m")){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "cjnet", options );
			return;
		}
		//取属性的方式line.getOptionProperties("T").get("boss")
//		System.out.println(line.getOptionProperties("T").get("boss"));
		
		if(StringUtil.isEmpty(line.getOptionValue("h")))
			throw new ParseException("参数-h是host为必需，但为空");
		if(!line.getOptionValue("h").contains(":")){
			throw new ParseException("-h 参数格式错误，正确为：ip:port");
		}
		if(StringUtil.isEmpty(line.getOptionValue("t")))
			throw new ParseException("参数-t是协议为必需，但为空，支持的协议有：udt,udp,tcp,http,websocket,local");
		String usr = System.getProperty("user.dir");
		File f = new File(usr);
		File[] arr = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith(fileName)) {
					return true;
				}
				return false;
			}
		});
		if (arr.length < 1 && !line.hasOption("debug")){
			throw new IOException(fileName+" 程序集不存在.");
		}
		if (line.hasOption("debug")) {
			File[] da = new File(line.getOptionValue("debug")).listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					if (name.startsWith(fileName)) {
						return true;
					}
					return false;
				}
			});
			if(da.length<0)
				throw new IOException("调试时不存在指定的必要jar包"+fileName);
			f =da[0];
		} else {
			f = arr[0];
		}
		
		IAssembly assembly = Assembly.loadAssembly(f.toString());
		assembly.start();
		Object main = assembly.workbin().part("main");
		IAdaptable a = (IAdaptable) main;
		IActuator act = a.getAdapter(IActuator.class);
		act.exeCommand("main", line);
	}
}
