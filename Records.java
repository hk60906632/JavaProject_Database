import java.util.LinkedList;

public class Records {
    private LinkedList<String> record = new LinkedList<String>();

    public void fill_records(String input){
        record.add(input);
    }

    public void replace_element(int index, String input){
        record.add(index, input);
    }

    public void remove(int index){
        record.remove(index);
    }

    public int record_size(){
        return record.size();
    }

    public String get_records(int index){
         return record.get(index);
    }

    public boolean check_exist(String input){
        return record.contains(input);
    }

    public int index_of(String input){
        return record.indexOf(input);
    }

}
