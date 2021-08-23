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
 * @create 2021-08-18 00:14
 */

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IdentityChangeMessage {
    private String type = Constants.IDENTITY_CHANGE_JSON_TYPE;
    private String identity;
}