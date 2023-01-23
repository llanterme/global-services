package za.co.digitalcowboy.global.services.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int userId;
    private String emailAddress;
    private String name;
    private String mobileNumber;
}
