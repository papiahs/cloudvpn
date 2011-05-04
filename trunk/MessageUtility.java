package edu.asu.eas.snac.cloudvpn.messages;

import java.util.ArrayList;

public class MessageUtility {
	
	public static Message convertTranssmitableMessageToMessage(TransmittableMessage msg){
		Message message = null;
		switch (msg.getMsgType()){
			case MessageType.FEEDBACK_MSG:
				FeedbackMessage feedbackMsg = new FeedbackMessage();
				feedbackMsg.setSeqNo(msg.getSeqNo());
				feedbackMsg.setMsgType(msg.getMsgType());
				feedbackMsg.setReturnNo((Integer) msg.getData()[0]);
				message = feedbackMsg;
				break;		
			case MessageType.LOGIN_MSG:
				LoginMessage loginMsg = new LoginMessage();
				loginMsg.setSeqNo(msg.getSeqNo());
				loginMsg.setMsgType(msg.getMsgType());
				loginMsg.setUsername((String)msg.getData()[0]);
				loginMsg.setPassword((String)msg.getData()[1]);
				message = loginMsg;
				break;
			case MessageType.REGISTER_MSG:
				RegisterMessage regMsg = new RegisterMessage();
				regMsg.setSeqNo(msg.getSeqNo());
				regMsg.setMsgType(msg.getMsgType());
				regMsg.setUsername((String)msg.getData()[0]);
				regMsg.setPassword((String)msg.getData()[1]);
				regMsg.setIMEI((String)msg.getData()[2]);
				regMsg.setEmail((String)msg.getData()[3]);
				regMsg.setPhone((String)msg.getData()[4]);
				message = regMsg;
				break;
		}
		return  message;
	}
	
	public static TransmittableMessage convertMessageToTransmittableMessage(Message msg){
		TransmittableMessage message = new TransmittableMessage();
		ArrayList<Object> dataList = new ArrayList<Object>();
		Object[] data = null;
		switch (msg.getMsgType()){
			case MessageType.FEEDBACK_MSG:
				FeedbackMessage feefbackMsg = (FeedbackMessage)msg;
				dataList.add(feefbackMsg.getReturnNo());
				data = dataList.toArray();
				break;
			case MessageType.LOGIN_MSG:
				LoginMessage loginMsg = (LoginMessage) msg;
				dataList.add(loginMsg.getUsername());
				dataList.add(loginMsg.getPassword());
				data = dataList.toArray();
				break;
			case MessageType.REGISTER_MSG:
				RegisterMessage regMsg = (RegisterMessage)msg;
				dataList.add(regMsg.getUsername());
				dataList.add(regMsg.getPassword());
				dataList.add(regMsg.getIMEI());
				dataList.add(regMsg.getEmail());
				dataList.add(regMsg.getPhone());
				data = dataList.toArray();
				break;
		}
		message.setSeqNo(msg.getSeqNo());
		message.setMsgType(msg.getMsgType());
		message.setData(data);
		return  message;
	}
	
}
