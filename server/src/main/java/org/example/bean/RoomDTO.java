package org.example.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 00:35
 */

@NoArgsConstructor
@Data
@ToString
public class RoomDTO {
    private String roomid;
    private Integer count;
}