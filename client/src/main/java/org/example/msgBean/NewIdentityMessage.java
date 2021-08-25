package org.example.msgBean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.utils.Constants;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewIdentityMessage {
    private String type = Constants.NEW_IDENTITY_JSON_TYPE;
    private String former;
    private String identity;
}