package digital.toke.event;

import java.util.EventObject;
import java.util.List;

import digital.toke.TokenRenewal;

public class RenewalTokenEvent extends TokenEvent {

	private static final long serialVersionUID = 1L;
	
	final EventEnum type = EventEnum.RELOAD_TOKEN;
	final List<TokenRenewal> list;

	public RenewalTokenEvent(Object arg0, List<TokenRenewal> list) {
		super(arg0, EventEnum.RENEWAL);
		this.list = list;
	}
	
	public EventEnum getType() {
		return type;
	}

	public List<TokenRenewal> getList() {
		return list;
	}
	
	

}
