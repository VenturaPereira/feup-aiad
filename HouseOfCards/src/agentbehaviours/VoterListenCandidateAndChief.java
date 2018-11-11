package agentbehaviours;

import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.util.ArrayList;
import java.util.HashMap;

import agents.Voter;
import jade.core.Agent;

public class VoterListenCandidateAndChief extends SimpleBehaviour {

	private Voter voter;
	private boolean finished = false;
	private boolean chiefReceived = false;

	public VoterListenCandidateAndChief(Voter voter) {
		this.voter = voter;
	}

	public void action() {

		ACLMessage msg = this.voter.blockingReceive();

		if (msg != null) {
			try {
				if (msg.getSender().getLocalName().substring(0, 9).equals("candidate")) {
					// System.out.println(" - VOTER: " +
					// this.voter.getLocalName() + " LISTENING CANDIDATE
					// BELIEFS: "
					// + msg.getSender().getLocalName() + " " +
					// msg.getContentObject());
					this.voter.logger
							.info("RECEIVED:  " + msg.getContentObject() + " FROM: " + msg.getSender().getLocalName());
					String candidate = msg.getSender().getLocalName();
					HashMap<String, Integer> beliefs = new HashMap<String, Integer>();
					HashMap<String, Integer> credibility = new HashMap<String, Integer>();
					ArrayList<HashMap<String, Integer>> profile = new ArrayList<HashMap<String, Integer>>();
					profile = (ArrayList) msg.getContentObject();
					this.voter.getCandidatesBeliefs().put(candidate, profile.get(0));
					this.voter.getCandidatesCredibility().put(msg.getSender().getLocalName(),
							profile.get(1).get("Credibility"));
					
				} else if (msg != null && msg.getSender().getLocalName().substring(0, 9).equals("chiefofst")) {

					try {
						this.voter.logger.info(
								"RECEIVED:  " + msg.getContentObject() + " FROM: " + msg.getSender().getLocalName());
						ArrayList<String> message = (ArrayList) msg.getContentObject();
						String candidate = message.get(1);
						this.voter.getChiefOfStaffInfo().put(msg.getSender().getLocalName(), candidate);
						chiefReceived = true;
					} catch (UnreadableException e) {
						e.printStackTrace();
					}
				}
				
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
		} else {
			block();
		}

		if (this.voter.getCandidatesBeliefs().size() == this.voter.getCandidatesSize() && chiefReceived) {
			this.voter.chooseCandidate();
			this.finished = true;
		}

		return;
	}

	public boolean done() {
		return this.finished;
	}

}