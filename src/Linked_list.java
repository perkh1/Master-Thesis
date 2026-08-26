public class Linked_list {
    private node first;
    private node last;
    public int leng;

    public void Linked_list(){
        first = null;
        last = null;
        leng = 0;
    }
    public Object get_first(){
        if (first != null){
            return first.data;
        }
        return null;
    }
    public void remove_first(){
        first = first.next;
        leng--;
    }
    public void add_last(Object data){
        if(last == null){
            node f = new node(data);
            first = f;
            last = f;
        }
        else {
            last.next = new node(data);
            last = last.next;
        }
        leng++;
    }
}
class node {
    Object data;
    node next;

    public node(Object data){
        this.data = data;
        this.next = null;
    }
}
