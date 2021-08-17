package org.example.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private String type = "newidentity";
    private String former;
    private String identity;
}