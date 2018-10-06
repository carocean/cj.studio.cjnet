package cj.studio.ecm.chip.command;

import org.apache.commons.cli.ParseException;

public interface ICmdLineParser {

	ICommandLine parse(String line)throws ParseException ;

}
