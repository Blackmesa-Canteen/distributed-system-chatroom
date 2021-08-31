package org.example.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.network.ServerConnection;

import java.util.ArrayList;
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
    // immutable
    private String roomId;
    private Client owner;
    private ArrayList<Client> clients;
}
