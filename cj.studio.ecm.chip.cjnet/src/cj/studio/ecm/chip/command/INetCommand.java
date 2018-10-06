package cj.studio.ecm.chip.command;

import cj.studio.ecm.graph.CircuitException;
import cj.studio.ecm.net.IClient;

public interface INetCommand {

	void exe(ICommandLine cl, IClient client)throws CircuitException ;

}
