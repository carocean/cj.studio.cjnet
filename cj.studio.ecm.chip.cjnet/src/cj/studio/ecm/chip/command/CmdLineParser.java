package cj.studio.ecm.chip.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CmdLineParser implements ICmdLineParser {
	String host;
	public CmdLineParser(String host) {
		this.host=host;
	}
	@SuppressWarnings("static-access")
	@Override
	public ICommandLine parse(String lineStr) throws ParseException {
		String[] arr=lineStr.split(" ");
		String args[] =new String[arr.length-1];
		if(arr.length>1)
			System.arraycopy(arr, 1, args, 0, arr.length-1);
		
		Options options = new Options();
		Option t = new Option("t", "protocol",true, "侦的请求头包含的协议。");
		options.addOption(t);
		Option u = new Option("u", "url",true, "侦的请求头包含的url。");
		options.addOption(u);
		Option c = new Option("c", "content",false, "侦的内容。");
		options.addOption(c);
		Option  m = new Option("m","man", false, "帮助");
		options.addOption(m);
		Option  s = new Option("s","sync", false, "指定为同步发送消息.默认是异步发送。\r\n如果是同步，消息将在命令窗口中交替显示，\r\n如果是异步，消息在到达时随机显示");
		options.addOption(s);
		Option h  = OptionBuilder.withArgName( "headName=value" )
                .hasArgs(2)
                .withValueSeparator()
                .withDescription( "设置侦的头，格式： \r\n-Hheadname=value" )
                .create( "H" );
		options.addOption(h);
		Option p  = OptionBuilder.withArgName( "paramName=value" )
                .hasArgs(2)
                .withValueSeparator()
                .withDescription( "设置侦的参数,格式为：-Pparamname=value" )
                .create( "P" );
		options.addOption(p);
		
		Option ch  = OptionBuilder.withArgName( "headName=value" )
                .hasArgs(2)
                .withValueSeparator()
                .withDescription( "设置回路的头,格式为：-CHheadName=value" )
                .create( "C" );
		options.addOption(ch);
//		GnuParser
//		BasicParser
//		PosixParser
		GnuParser parser = new GnuParser();
		CommandLine line = parser.parse(options, args);
		if(line.hasOption("m")){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( arr[0], options );
			String prev = NetConsole.COLOR_CMDPREV + host + " >"
					+ NetConsole.COLOR_CMDLINE;
			System.out.print(prev);
			return null;
		}
		if(!line.hasOption("t"))
			throw new ParseException("必须指定协议.-t");
		if(!line.hasOption("u"))
			throw new ParseException("必须指定url.-u");
		return new Line(arr[0],line);
	}
	class Line implements ICommandLine{
		String cmd;
		CommandLine line;
		public Line(String string, CommandLine line) {
			cmd=string;
			this.line=line;
		}
		@Override
		public String cmd() {
			// TODO Auto-generated method stub
			return cmd;
		}
		@Override
		public CommandLine line() {
			// TODO Auto-generated method stub
			return line;
		}
	}
}
