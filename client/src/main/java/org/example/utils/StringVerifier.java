package org.example.utils;

public class StringVerifier {

    public static boolean isValidRoomId(String roomId) {
        if (roomId.length() >= 3 && roomId.length() <= 32) {
            String regex = "^[a-zA-Z]+[A-Za-z0-9]+$";
            return roomId.matches(regex);
        }

        return false;
    }

    public static boolean isValidClientId(String clientId) {
        if (clientId.length() >= 3 && clientId.length() <= 16) {
            String regex = "^[a-zA-Z]+[A-Za-z0-9]+$";
            return clientId.matches(regex);
        }

        return false;
    }
}
