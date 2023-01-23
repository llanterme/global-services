package za.co.digitalcowboy.global.services.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SqsResponse implements Serializable {

	private String signatureVersion;
	private String type;
	private String topicArn;
	private String Message;
	private String unsubscribeURL;
	private String signature;
	private String timestamp;
	private String signingCertURL;
	private String messageId;


}
