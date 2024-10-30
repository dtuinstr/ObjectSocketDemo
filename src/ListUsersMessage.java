public class ListUsersMessage
        extends Message
{
    public ListUsersMessage(String username)
    {
        super(username, MsgType.LISTUSERS);
    }
}
