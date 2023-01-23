package za.co.digitalcowboy.global.services.domain;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoredResponse {


	private String activity;
	private int accessibility;
	private int price;
	private String link;
	private String type;
	private String key;
	private int participants;


}