package cj.studio.ecm.chip.command;

import java.io.UnsupportedEncodingException;

import cj.studio.ecm.frame.Circuit;
import cj.studio.ecm.frame.Frame;
import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.graph.IPlug;
import cj.studio.ecm.graph.ISink;
import cj.ultimate.util.StringUtil;

class Responser implements ISink {
		String host;

		public Responser(String host) {
			this.host = host;
		}

		@Override
		public void flow(Frame frame,Circuit circuit, IPlug plug) throws CircuitException {
			StringBuffer sb = new StringBuffer();
			sb.append(NetConsole.COLOR_RESPONSE);
			
			if ("connect".equals(frame.command())) {
				sb.append("\r\nanswer:\r\n");
				printFrame(frame, sb);
				String prev = NetConsole.COLOR_CMDPREV + host + " >"
						+ NetConsole.COLOR_CMDLINE;
				sb.append(prev);
				System.out.print(sb.toString());
				return;
			}
			if (!"disconnect".equals(frame.command())) {
				sb.append("async answer:\r\n");
				printFrame(frame, sb);
				String prev = NetConsole.COLOR_CMDPREV + host + " >"
						+ NetConsole.COLOR_CMDLINE;
				sb.append(prev);

			} else {
				sb.append("async answer:\r\n");
				printFrame(frame, sb);
				sb.append(NetConsole.COLOR_SURFACE);
				sb.append("disconnect...\r\n");
			}
			System.out.print(sb.toString());
		}

		public static void printFrame(Frame frame, StringBuffer sb) {
			sb.append("\tline:\t" + frame.toString() + "\r\n");
			sb.append("\thead:\r\n");
			for (String h : frame.enumHeadName()) {
				sb.append("\t\t" + h + "=" + frame.head(h) + "\r\n");
			}
			sb.append("\tparam:\r\n");
			if (frame.enumParameterName().length < 1)
				sb.append("\t\t无参数\r\n");
			for (String h : frame.enumParameterName()) {
				sb.append("\t\t" + h + "=" + frame.parameter(h) + "\r\n");
			}
			if (frame.content().readableBytes() < 1) {
				sb.append("\r\n-----------------------------无内容--------------------------\r\n");
			}
			if (frame.content().readableBytes() > 0) {
				byte[] data = frame.content().readFully();
				String charset = "utf-8";
				if (!StringUtil.isEmpty(frame.contentChartset()))
					charset = frame.contentChartset();
				try {
					String cont = new String(data, charset);
					sb.append("\r\n---------------------content----------------------------\r\n");
					sb.append(cont);
					sb.append("\r\n---------------------content--end-----------------------");
				} catch (UnsupportedEncodingException e) {
					System.out.println("本地不支持该内容解码。");
				}
				sb.append("\r\n");
			}
		}
	}