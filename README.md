The assignment is to create a “chat” application, using the client / server architectural model. The chat system consists of two main distributed components: chat server and chat client, which may run on different hosts in the network. Chat clients are Java programs which can connect to a chat server.

The chat server is a Java application which can accept multiple incoming TCP connections. The chat server maintains a list of current chat rooms and chat clients can move between chat rooms. Messages sent by a chat client are broadcasted to all clients currently connected to the same chat room.

![image](https://user-images.githubusercontent.com/69796042/169295039-b8752087-3237-461b-80ff-96cae8af9d58.jpeg)
