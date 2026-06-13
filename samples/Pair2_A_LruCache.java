import java.util.HashMap;
import java.util.Map;
public class LruCache
{
    private static class Node
    {
        int key;
        String value;
        Node prev;
        Node next;
        Node(int key, String value)
        {
            this.key = key;
            this.value = value;
        }
    }
    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(-1, "");
    private final Node tail = new Node(-1, "");
    public LruCache(int capacity)
    {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }
    public String get(int key)
    {
        Node found = map.get(key);
        if(found == null)
        {
            return null;
        }
        remove(found);
        addFirst(found);
        return found.value;
    }
    public void put(int key, String value)
    {
        Node old = map.get(key);
        if(old != null)
        {
            old.value = value;
            remove(old);
            addFirst(old);
            return;
        }
        Node node = new Node(key, value);
        map.put(key, node);
        addFirst(node);
        if(map.size() > capacity)
        {
            Node victim = tail.prev;
            remove(victim);
            map.remove(victim.key);
        }
    }
    private void addFirst(Node node)
    {
        Node first = head.next;
        head.next = node;
        node.prev = head;
        node.next = first;
        first.prev = node;
    }
    private void remove(Node node)
    {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    public String snapshot()
    {
        StringBuilder text = new StringBuilder();
        Node cursor = head.next;
        while(cursor != tail)
        {
            text.append(cursor.key).append("=").append(cursor.value);
            if(cursor.next != tail)
            {
                text.append(", ");
            }
            cursor = cursor.next;
        }
        return text.toString();
    }
    public static void main(String[] args)
    {
        LruCache cache = new LruCache(3);
        cache.put(1, "alpha");
        cache.put(2, "beta");
        cache.put(3, "gamma");
        cache.get(1);
        cache.put(4, "delta");
        cache.put(5, "epsilon");
        System.out.println(cache.snapshot());
    }
}



