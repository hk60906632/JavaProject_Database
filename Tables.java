import java.util.*;
import java.io.*;

public class Tables {

    private LinkedList<Records> table = new LinkedList<Records>();
    private LinkedList<Records> data_type = new LinkedList<Records>();
    private int unique_key;


    public void add_fieldname(String[] words){
        int counter = 2;

        while (counter != words.length ){
            Records input = new Records();
            if (!words[counter].equals("(") && !words[counter].equals(")")){
                input.fill_records(words[counter]);
            }
            counter++;
            if (words[counter].equals("(")){
                counter++;
                while(!words[counter].equals(")")){
                    input.fill_records(words[counter]);
                    counter++;
                }
            }
            data_type.add(input);
            counter++;
        }

        Records record = new Records();
        for(int i = 0; i < data_type.size(); i++){
            record.fill_records(data_type.get(i).get_records(0));
        }
        table.add(record);

    }


    public void add_record(String[] words) {
        int counter = 1;
        Records record = new Records();

        if (!table.get(0).get_records(0).equals("Auto_Key") && (words.length - 1) != table.get(0).record_size()) {
            System.out.printf("###############Record data size doesn't matches the column number#############%n%n");
            return;
        }

        if (table.get(0).get_records(0).equals("Auto_Key") && (words.length - 1) != table.get(0).record_size() - 1){
            System.out.printf("###############Record data size doesn't matches the column number#############%n%n");
            return;
        }

        while (counter < words.length) {
            record.fill_records(words[counter]);
            counter++;
        }

        table.add(record);
        int row = table.size()-1;


        if (table.get(0).get_records(0).equals("Auto_Key")) {

            table.get(table.size() - 1).replace_element(0, Integer.toString(unique_key + 1));
            unique_key++;
        }

        for (int i = 0; i < table.get(0).record_size() ; i++){
            check_type(row , i);
        }

        System.out.println();

    }


    public void add_column(String[] words){

        int counter_1 = 0;
        int counter_2 = 2;

        if (Integer.valueOf(words[1]) == 0 && table.get(0).get_records(0).equals("Auto_Key")){
            System.out.println("############First column can be used by Auto_Key only################");
            return;
        }

        while(!words[counter_2].equals("[")){
            table.get(counter_1).replace_element(Integer.valueOf(words[1]), words[counter_2]);
            counter_1++;
            counter_2++;
        }


        counter_2++;
        while (counter_2 != words.length -1){
            Records input = new Records();
            if (!words[counter_2].equals("(") && !words[counter_2].equals(")")){
                input.fill_records(words[counter_2]);
            }
            counter_2++;
            if (words[counter_2].equals("(")){
                counter_2++;
                while(!words[counter_2].equals(")")){
                    input.fill_records(words[counter_2]);
                    counter_2++;
                }
            }
            data_type.add(Integer.valueOf(words[1]),input);
            counter_2++;
        }
        for(int i = 1; i < table.size(); i++){
            check_type(i, Integer.valueOf(words[1]));
        }

    }


    public void auto_gen_key(){
        if (table.get(0).get_records(0).equals("Auto_Key")){
            System.out.println("Auto_Key already exist");
            return;
        }

        table.get(0).replace_element(0, "Auto_Key");

        for (int counter = 1; counter < table.size(); counter++){
            table.get(counter).replace_element(0, Integer.toString(counter));
            unique_key = counter;
        }
        Records input = new Records();
        input.fill_records("Auto_Key");
        input.fill_records("INT");
        data_type.add(0,input);
    }




    public LinkedList<Records> check_foreign_key(){
        LinkedList<Records> foreign_table = new LinkedList<Records>();
        for(Records i : data_type){
            if(i.check_exist ("FOREIGN_KEY")){
                Records details = new Records();
                details.fill_records (Integer.toString(data_type.indexOf(i)));
                details.fill_records(i.get_records(i.index_of("FOREIGN_KEY")+1));
                //return i.get_records(i.index_of("FOREIGN_KEY")+1);
                foreign_table.add(details);
            }
        }
        //for(Records j : foreign_table){
            //System.out.println(j.get_records(0));
            //System.out.println(j.get_records(1));
        //}
        return foreign_table;
    }

    public LinkedList<String> copy_key_column(){
        LinkedList<String> buffer = new LinkedList<String>();

            for (int i = 0; i < table.size(); i++) {
                buffer.add(table.get(i).get_records(0));
            }
            return buffer;
    }

    public void check_for_key(LinkedList<String> keys, int row_num){
        for (int counter = 1; counter < table.size(); counter++){
            if (!keys.contains(table.get(counter).get_records(row_num))){
                table.get(counter).remove(row_num);
                table.get(counter).replace_element(row_num, "NULL");
            }

        }

    }

    public Boolean check_for_key_exist(){
        for(Records i : data_type){
            if(i.check_exist ("FOREIGN_KEY")){
                return true;
            }
        }
        return false;
    }




    public boolean check_auto_key_exist(){ return table.get(0).get_records(0).equals("Auto_Key"); }



    public void check_type(int row, int col){
        switch (data_type.get(col).get_records(1)){
            case "INT":
                try{
                    Integer.parseInt(table.get(row).get_records(col));
                }catch(Exception e){
                    table.get(row).remove(col);
                    table.get(row).replace_element(col, "NULL");
                }
                break;

            case "FLOAT":
                try{
                    Float.parseFloat(table.get(row).get_records(col));
                }catch(Exception e){
                    table.get(row).remove(col);
                    table.get(row).replace_element(col, "NULL");
                }
                break;

            case "ENUM":
                int counter = 0, k = 2;
                while (k <= (data_type.get(col).record_size() - 1)){
                    //System.out.print(field_name.get(i).get_records(k));
                    if (table.get(row).get_records(col).equals(data_type.get(col).get_records(k))){
                        counter++;
                    }
                    k++;
                }

                if (counter == 0){
                    table.get(row).remove(col);
                    table.get(row).replace_element(col, "NULL");
                }
                break;

            case "FOREIGN_KEY":
                try{
                    Integer.parseInt(table.get(row).get_records(col));
                }catch(Exception e){
                    table.get(row).remove(col);
                    table.get(row).replace_element(col, "NULL");
                }
                break;

        }
    }



    public void delete_record(int row) {
        table.remove(row);
    }

    public void delete_n_record(int start, int end) {
        int counter = 0;
        assert (start < end);
        assert (start >= 1);
        assert (end < table.size());

        while (counter <= (end - start)) {
            table.remove(start);
            counter++;
        }
    }




    public void delete_column(String field_name) {
        int index;

        if (table.get(0).check_exist(field_name)){
            index = table.get(0).index_of(field_name);
        }else{
            System.out.println("Error!!!non-existed column");
            return;
        }

        if (table.get(0).get_records(index).equals("Auto_Key") && index == 0){
            System.out.println("Auto_Key column cannot be alter");
            return;
        }

        for (Records i : table){
            i.remove(index);
        }
        data_type.remove(index);
    }


    public void delete_n_column(String input1, String input2) {
        int counter = 0;
        int start;
        int end;

        if (table.get(0).check_exist(input1) && table.get(0).check_exist(input2)){
            start = table.get(0).index_of(input1);
            end = table.get(0).index_of(input2);
        }else{
            System.out.println("Error!!!non-existed column");
            return;
        }

        assert(start < end);
        assert(end < table.get(0).record_size());

        if (table.get(0).get_records(0).equals("Auto_Key") && start == 0){
            System.out.println("Auto_Key column cannot be alter");
            return;
        }

        for (Records i : table){
            while (counter <= (end - start)) {
                i.remove(start);
                counter++;
            }
            counter = 0;
        }
       while (counter <= (end - start)){
           data_type.remove(start);
           counter++;
       }
    }

    private void fill_up_type(Scanner sc){
        String s, buffer;
        while (!(s = sc.next()).equals(";")) {
            Records input = new Records();
            input.fill_records(s);
            if (sc.next().equals("(")){
                while(!(buffer = sc.next()).equals(")")){
                    input.fill_records(buffer);
                }
            }
            data_type.add(input);
        }


        System.out.println();
    }



    private void fill_up_record(Scanner sc){
        String s;
        Records record = new Records();
        if (sc.hasNext()){
            record.fill_records(sc.next());
        }else{
            return;
        }
        while (!(s = sc.next()).equals(";")) {
            record.fill_records(s);
        }
        table.add(record);
        fill_up_record(sc);
    }



    public void load_file(String file_path){
        try{
            Scanner sc = new Scanner(new File(file_path));
            fill_up_type(sc);
            fill_up_record(sc);
            for (int i = 0; i < table.get(0).record_size(); i++) {
                for (int j = 1; j < table.size(); j++) {
                    check_type(j, i);
                }
            }

        }catch(Exception e){
            System.out.println("Error can't get file ");
        }

        if (table.get(0).get_records(0).equals("Auto_Key")){
            unique_key = Integer.valueOf(table.get(table.size() - 1).get_records(0));
        }
    }




    public void print_table() {

        System.out.printf("|%-7s| ", "Row");
        for (int k = 0; k < table.get(0).record_size(); k++) {
            //System.out.print("  " + table.get(0).get_records(k) + "  "  + "|");
            System.out.printf("|%-15s| ", table.get(0).get_records(k));
        }

        System.out.println();
        System.out.println();

        for (int i = 1; i < table.size(); i++) {
            System.out.printf("|%-7s| ", i);
            for (int j = 0; j < table.get(i).record_size(); j++) {
                //System.out.print("  " + table.get(i).get_records(j) + "  " + "|");
                System.out.printf("|%-15s| ", table.get(i).get_records(j));
            }
            System.out.println();

        }
        System.out.println();
    }


    public void print_enum_type(){
        System.out.printf("|%-15s||%-15s| |%-15s| %n","Col_number", "Col_name", "Col_type");
        for(int i = 0; i < data_type.size(); i++){
            System.out.printf("|%-15s|",i);
            for(int j = 0; j < data_type.get(i).record_size(); j++){
                System.out.printf("|%-15s| ",data_type.get(i).get_records(j));
                //System.out.print(enum_type.get(i).get_records(j)+" " );
            }
            System.out.println();
        }
        System.out.println();
    }



    public void search_record_by_key(String[] words){
        if (table.get(0).get_records(0).equals("Auto_Key")){
            for (int i = 1; i < table.size(); i++){
                if(table.get(i).get_records(0).equals(words[1])){

                    for (int k = 0; k < table.get(0).record_size(); k++) {
                        System.out.printf("|%-15s| ", table.get(0).get_records(k));
                    }

                    System.out.println();

                    for (int j = 0; j < table.get(i).record_size(); j++){
                        System.out.printf("|%-15s| ", table.get(i).get_records(j));
                    }
                    System.out.println();
                }
            }
        }else{
            System.out.println("Error!!!!!There is no Auto_Key in the table!!!!");
        }
    }




    public void save(String file_path, boolean append_to_file) throws IOException {
        FileWriter write = new FileWriter(file_path, append_to_file);
        PrintWriter print_line = new PrintWriter(write);
        Scanner input = new Scanner(new File(file_path));

        while(input.hasNext()){
            print_line.print(" ");
        }

        for (Records k : data_type){
            print_line.print(k.get_records(0) + " " + "(" + " ");
            for (int l = 1; l < k.record_size(); l++){
                print_line.print(k.get_records(l) + " ");
            }
            print_line.print(")" + " ");
        }
        print_line.print(";");
        print_line.println();


        for (Records i : table){
            for (int j = 0; j < i.record_size(); j++) {
                print_line.print(i.get_records(j) + " ");
            }
            print_line.print(";");
            print_line.println();

        }

        print_line.close();

    }



    public void save_as(String file_path, String file_name) throws IOException {
        String path = file_path + file_name;

        File file = new File(path);
        file.createNewFile();

        FileWriter write = new FileWriter(path);
        PrintWriter print_line = new PrintWriter(write);

        for (Records k : data_type){
            print_line.print(k.get_records(0) + " " + "(" + " ");
            for (int l = 1; l < k.record_size(); l++){
                print_line.print(k.get_records(l) + " ");
            }
            print_line.print(")" + " ");
        }
        print_line.print(";");
        print_line.println();


        for (Records i : table){
            for (int j = 0; j < i.record_size(); j++) {
                print_line.print(i.get_records(j) + " ");
            }
            print_line.print(";");
            print_line.println();

        }
        print_line.close();

    }


    public void update_cell(int col, int row, String replacement) {
        if (table.get(0).get_records(0).equals("Auto_Key") && col == 0){
            System.out.println("Auto_Key column cannot be alter");
            return;
        }
        table.get(row).remove(col);
        table.get(row).replace_element(col, replacement);
        check_type(row, col);
    }



    public int row_number() { return table.size(); }



    public int col_number() { return table.get(0).record_size(); }


    public static void main(String args[]){

        //here i have tested the add_fieldname(), add_record(), auto_gen_key(), add_column(), delete_column(), delete_n_column()
        //delete_record, delete_n_record and check_type()
        //the way i test is load in some test data and make sure the linkedlist has the correct set of data
        Tables test = new Tables();
        String[] field_name = {"ADD_TABLE", "Test1", "First_name", "(" , "STRING", ")", "last_name", "(", "STRING" ,")","Age","(","INT",")", "Test_Float", "(", "FLOAT", ")"};
        test.add_fieldname(field_name);
        assert(test.table.size() == 1);
        assert(test.data_type.size() == 4);
        assert(test.data_type.get(0).record_size() == 2);
        assert(test.data_type.get(1).record_size() == 2);
        assert(test.data_type.get(2).record_size() == 2);
        assert(test.data_type.get(3).record_size() == 2);
        assert(test.data_type.get(0).get_records(1).equals("STRING"));
        assert(test.data_type.get(2).get_records(1).equals("INT"));
        assert(test.data_type.get(3).get_records(1).equals("FLOAT"));

        String[] words = {"ADD_RECORD","Jessy", "Cena", "35", "3.3"};
        test.add_record(words);
        test.print_table();
        assert(test.table.size() == 2);
        assert(test.table.get(1).record_size() == 4);
        assert(test.table.get(1).get_records(1).equals("Cena"));

        test.auto_gen_key();
        String[] words2 = {"ADD_RECORD","Kenny", "G", "50", "18"};
        test.add_record(words2);
        assert(test.table.size() == 3);
        assert(test.data_type.get(0).get_records(0).equals("Auto_Key"));
        assert(test.table.get(1).record_size() == 5);
        assert(test.table.get(2).record_size() == 5);
        assert(test.table.get(2).get_records(2).equals("G"));
        assert(test.table.get(2).get_records(0).equals("2")); //make sure the auto gen key works for get_records()


        String[] words3 = {"ADD_RECORD","Taylor", "Swift", "50", "a"};
        test.add_record(words3);
        test.print_table();
        assert(test.table.get(3).get_records(4).equals("NULL")); //this check the data type checker works
        assert(test.table.get(3).get_records(0).equals("3"));


        String[] words4 = {"ADD_COL", "3", "GFable","Yea", "Nah", "Alright", "[", "GFable", "(","ENUM","Yea","Nah","Alright",")", "]"};
        test.add_column(words4);
        assert(test.table.get(0).record_size() == 6);
        assert(test.table.get(1).record_size() == 6);
        assert(test.table.get(2).record_size() == 6);
        assert(test.table.get(3).record_size() == 6);
        assert(test.table.get(0).get_records(3).equals("GFable"));
        assert(test.table.get(1).get_records(3).equals("Yea"));
        assert(test.table.get(2).get_records(3).equals("Nah"));
        assert(test.table.get(3).get_records(3).equals("Alright"));
        assert(test.data_type.get(3).record_size() == 5); //test make sure it take in all the enum type
        assert(test.data_type.get(3).get_records(2).equals("Yea"));
        assert(test.data_type.get(3).get_records(4).equals("Alright"));



        String[] words5 = {"ADD_COL", "4", "Going","N", "Naf", "Y", "[", "Going", "(","ENUM","Y","N",")", "]"};
        test.add_column(words5);
        test.print_table();
        test.print_enum_type();
        assert(test.table.get(0).record_size() == 7);
        assert(test.table.get(1).record_size() == 7);
        assert(test.table.get(2).record_size() == 7);
        assert(test.table.get(3).record_size() == 7);
        assert(test.table.get(2).get_records(4).equals("NULL")); //make sure type check works for add_column()

        String[] words6 = {"ADD_RECORD", "Jessica", "Jones","test_error", "Y", "2.6", "3.3"};
        test.add_record(words6);
        test.print_table();
        assert(test.table.get(4).get_records(3).equals("NULL"));//test type checker
        assert(test.table.get(4).get_records(5).equals("NULL"));
        assert(test.table.get(4).get_records(6).equals("3.3"));

        test.delete_column("Test_Float");
        test.print_table();
        assert(test.table.get(0).record_size() == 6);
        assert(test.table.get(1).record_size() == 6);
        assert(test.table.get(2).record_size() == 6);
        assert(test.table.get(3).record_size() == 6);
        assert(test.table.get(4).record_size() == 6);
        assert(!test.table.get(0).check_exist("Test_Float"));
        assert(test.data_type.size() == 6);
        assert(test.data_type.get(0).get_records(0).equals("Auto_Key"));
        assert(test.data_type.get(1).get_records(0).equals("First_name"));
        assert(test.data_type.get(2).get_records(0).equals("last_name"));
        assert(test.data_type.get(3).get_records(0).equals("GFable"));
        assert(test.data_type.get(4).get_records(0).equals("Going"));
        assert(test.data_type.get(5).get_records(0).equals("Age"));

        test.delete_n_column("Going", "Age");
        test.print_table();
        assert(test.table.get(0).record_size() == 4);
        assert(test.table.get(1).record_size() == 4);
        assert(test.table.get(2).record_size() == 4);
        assert(test.table.get(3).record_size() == 4);
        assert(test.table.get(4).record_size() == 4);
        assert(!test.table.get(0).check_exist("Going"));
        assert(!test.table.get(0).check_exist("Age"));
        assert(test.data_type.size() == 4);
        assert(test.data_type.get(0).get_records(0).equals("Auto_Key"));
        assert(test.data_type.get(1).get_records(0).equals("First_name"));
        assert(test.data_type.get(2).get_records(0).equals("last_name"));
        assert(test.data_type.get(3).get_records(0).equals("GFable"));


        test.delete_record(2);
        test.print_table();
        assert(test.table.size() == 4);
        assert(test.table.get(1).get_records(0).equals("1"));
        assert(test.table.get(2).get_records(0).equals("3"));
        assert(test.table.get(3).get_records(0).equals("4"));


        test.delete_n_record(1, 3);
        test.print_table();
        assert(test.table.size() == 1);

        System.out.println("##################################################################################");
        System.out.println("Save and load test");
        System.out.println();

        //Here i test the load and save function
        //first load a data txt file into my database, do some test make sure all the data load in are right
        //then save the file back to the same file and carry our the same test as before
        //this make sure the save() function write everything into the file correctly

        Tables test2 = new Tables();
        test2.load_file("/home/kevin/Documents/test1.txt");//<-----Change the directory
        System.out.printf("%n Test2 %n");
        test2.print_table();
        assert(test2.table.size() == 5);
        assert(test2.data_type.size() == 4);
        assert(test2.table.get(1).get_records(2).equals("NULL"));
        assert(test2.table.get(3).get_records(3).equals("NULL"));
        assert(test2.table.get(0).get_records(0).equals("First_name"));
        assert(test2.table.get(0).get_records(1).equals("Last_name"));
        assert(test2.table.get(0).get_records(2).equals("Age"));
        assert(test2.table.get(0).get_records(3).equals("Gender"));
        assert(test2.table.get(1).get_records(0).equals("Kevin"));
        assert(test2.table.get(4).get_records(1).equals("wong"));
        assert(test2.table.get(2).get_records(2).equals("20"));
        assert(test2.table.get(1).get_records(3).equals("M"));
        assert(test2.data_type.get(3).record_size() == 4);
        assert(test2.data_type.get(3).get_records(2).equals("M"));

        try{
            test2.save("/home/kevin/Documents/test1.txt", false);//<-----Change the directory

        }catch(Exception e){
            System.out.println("Error");
        }
        Tables test3 = new Tables();
        test3.load_file("/home/kevin/Documents/test1.txt");//<-----Change the directory
        System.out.printf("%n Test3 %n");
        test3.print_table();
        assert(test3.table.size() == 5);
        assert(test3.data_type.size() == 4);
        assert(test3.table.get(1).get_records(2).equals("NULL"));
        assert(test3.table.get(3).get_records(3).equals("NULL"));
        assert(test3.table.get(0).get_records(0).equals("First_name"));
        assert(test3.table.get(0).get_records(1).equals("Last_name"));
        assert(test3.table.get(0).get_records(2).equals("Age"));
        assert(test3.table.get(0).get_records(3).equals("Gender"));
        assert(test3.table.get(1).get_records(0).equals("Kevin"));
        assert(test3.table.get(4).get_records(1).equals("wong"));
        assert(test3.table.get(2).get_records(2).equals("20"));
        assert(test3.table.get(1).get_records(3).equals("M"));
        assert(test3.data_type.get(3).record_size() == 4);
        assert(test3.data_type.get(3).get_records(2).equals("M"));

    }


}
