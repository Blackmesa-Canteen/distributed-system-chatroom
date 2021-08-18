package org.example.msgBean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:14
 */

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IdentityChangeMessage {
    private String type = "identitychange";
    private String identity;
}