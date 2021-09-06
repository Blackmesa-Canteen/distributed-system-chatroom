package org.example.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.msgBean.RoomDTO;
import org.example.network.ServerConnection;
import org.example.utils.Constants;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Xiaotian
 * @program assignment1
 * @description
 * @create 2021-08-18 19:37
 */

@Data
@NoArgsConstructor
@ToString
public class Client {
    private String id;
    private String formerId;
    private String formerRoomId;
    private String roomId;
    private ServerConnection serverConnection;
    private ArrayList<String> roomlist;
    private String tempRoomName;
    private String status = Constants.START_STATUS;
    private String tempRoomContent;
    private List<RoomDTO> roomDTOList;

    public String printroomDTOlist(){
        String result = "";
        for(RoomDTO RDTO:roomDTOList){
            result += RDTO.getRoomid()+": "+RDTO.getCount()+" guests\n";
        }
        return result.trim();
    }
    public ArrayList<String> printRoomList(){
        ArrayList<String> result = new ArrayList<>();
        for(RoomDTO RDTO:roomDTOList){
            result.add(RDTO.getRoomid());
        }
        return result;
    }
}