import java.util.HashMap;
import java.util.Map;
public class RecentItems
{
    private static class Entry
    {
        int id;
        String name;
        Entry left;
        Entry right;
        Entry(int id, String name)
        {
            this.id = id;
            this.name = name;
        }
    }
    private final int limit;
    private final Map<Integer, Entry> lookup = new HashMap<>();
    private final Entry front = new Entry(-1, "");
    private final Entry back = new Entry(-1, "");
    public RecentItems(int limit)
    {
        this.limit = limit;
        front.right = back;
        back.left = front;
    }
    public String open(int id)
    {
        Entry item = lookup.get(id);
        if(item == null)
        {
            return null;
        }
        unlink(item);
        moveFront(item);
        return item.name;
    }
    public void remember(int id, String name)
    {
        Entry existing = lookup.get(id);
        if(existing != null)
        {
            existing.name = name;
            unlink(existing);
            moveFront(existing);
            return;
        }
        Entry fresh = new Entry(id, name);
        lookup.put(id, fresh);
        moveFront(fresh);
        if(lookup.size() > limit)
        {
            Entry dropped = back.left;
            unlink(dropped);
            lookup.remove(dropped.id);
        }
    }
    private void moveFront(Entry item)
    {
        Entry first = front.right;
        front.right = item;
        item.left = front;
        item.right = first;
        first.left = item;
    }
    private void unlink(Entry item)
    {
        item.left.right = item.right;
        item.right.left = item.left;
    }
    public String show()
    {
        StringBuilder builder = new StringBuilder();
        Entry cursor = front.right;
        while(cursor != back)
        {
            builder.append(cursor.id).append("=").append(cursor.name);
            if(cursor.right != back)
            {
                builder.append(", ");
            }
            cursor = cursor.right;
        }
        return builder.toString();
    }
    public static void main(String[] args)
    {
        RecentItems recent = new RecentItems(3);
        recent.remember(1, "alpha");
        recent.remember(2, "beta");
        recent.remember(3, "gamma");
        recent.open(1);
        recent.remember(4, "delta");
        recent.remember(5, "epsilon");
        System.out.println(recent.show());
    }
}



