package org.example.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 19:35
 */

@Data
@NoArgsConstructor
@ToString
public class Room {
    private String roomId;
    private String ownerId;
    private Map<String, String> guestConnections;
}