package org.example.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:40
 */

@Data
@NoArgsConstructor
@ToString
public class QuitMessage {
    private String type = "quit";
}