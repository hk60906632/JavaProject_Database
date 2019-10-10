import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

public class Databases {
    private LinkedList<Tables> database = new LinkedList<Tables>();
    private LinkedList<String> table_name = new LinkedList<String>();

    private int index;

    private void add_tables_byfile(String file_path, String table_name_input){
        Tables table = new Tables();
        table.load_file(file_path);
        table_name.add(table_name_input);
        database.add(table);
    }

    private void add_table(String table_name_input, String[] words){
        Tables table = new Tables();
        table_name.add(table_name_input);
        table.add_fieldname(words);
        database.add(table);
    }

    private void delete_table(String name_of_table){
        if (index == table_name.indexOf(name_of_table)){
            System.out.println("Please exit the table first then delete it ");
            return;
        }
        if(table_name.contains(name_of_table)){
            database.remove(table_name.indexOf(name_of_table));
            table_name.remove(table_name.indexOf(name_of_table));
        }else {
            System.out.println("Not matching name table in data base");
        }
    }


    private void select_table(String name_of_table){

        if(table_name.contains(name_of_table)){
            index = table_name.indexOf(name_of_table);
        }else {
            System.out.println("Not matching name table in data base");
        }

    }

    private void list_table(){
        System.out.printf("%n%n");
        System.out.printf("|%-20s| %n", "Table_list");
        for (int i = 0; i < table_name.size(); i++){
            System.out.printf("|%-20s| %n", table_name.get(i));
        }
        System.out.printf("%n%n");
    }



    private void load_command(Scanner sc){
        String s;
        LinkedList<String> command = new LinkedList<String>();
        if (sc.hasNext()){
            command.add(sc.next());
        }else{
            return;
        }
        while (!(s = sc.next()).equals(";")) {
            command.add(s);
        }
        String[] words = command.toArray(new String[command.size()]);

        commands(words);

        load_command(sc);
    }



    private void file_parser(String file_path){
        try{
            Scanner sc = new Scanner(new File(file_path));
            load_command(sc);
        }catch(Exception e){
            System.out.println("Error can't get file ");
        }

    }



    public void parser(){
        Scanner input = new Scanner(System.in);

        if(table_name.size() > 0){
            System.out.printf("<%s>:" , table_name.get(index));
        }else{
            System.out.println("Database is empty, please add table to the database:");
        }


        String input_command = input.nextLine();
        String[] words = input_command.split("\\s+");

        if(input_command.equals("EXIT_DATABASE")){
            return;
        }

        commands(words);

        parser();

    }

    private void check_foreign_key(){
        for (Tables i : database){
            if (i.check_for_key_exist()){
                LinkedList<Records> foreign_table = i.check_foreign_key();
                if (foreign_table.size() > 0){
                    for (Records j : foreign_table){
                        if (table_name.contains(j.get_records(1))){
                            if(database.get(table_name.indexOf(j.get_records(1))).check_auto_key_exist()){

                                String foreign_table_name = j.get_records(1);
                                LinkedList<String> key = database.get(table_name.indexOf(foreign_table_name)).copy_key_column();

                                i.check_for_key(key, Integer.valueOf(j.get_records(0)));

                            }
                            else{
                                LinkedList<String> turn_null = new LinkedList<String>();
                                i.check_for_key(turn_null, Integer.valueOf(j.get_records(0)));
                                System.out.println();
                                System.out.println("#############################################################################################################################################");
                                System.out.println("####################ERROR!!!!! For the foreign key inside ("+ table_name.get(database.indexOf(i)) +") :: <"+ foreign_table +">"+"doesn't have AUTO_GEN_KEY, please add AUTO_GEN_KEY#############");
                                System.out.println("#############################################################################################################################################");
                                System.out.println();
                            }
                        }
                        else{
                            LinkedList<String> turn_null = new LinkedList<String>();
                            i.check_for_key(turn_null, Integer.valueOf(j.get_records(0)));
                            System.out.println();
                            System.out.println("#################################################################################################################################################");
                            System.out.println("####################ERROR!!!!! For the foreign key inside ("+table_name.get(database.indexOf(i))+") :: The table of the foreign key is not exist!!!!!Please insert " +"<"+ foreign_table +">"+ "#############");
                            System.out.println("#################################################################################################################################################");
                            System.out.println();

                        }


                    }
                }

            }
        }
    }


    private void commands(String[] words){
        switch (words[0]) {

            case "AUTO_GEN_KEY":
                try{
                    database.get(index).auto_gen_key();
                }catch (Exception e){
                    System.out.print("<AUTO_GEN_KEY>   ");
                    System.out.println(e);
                }
                break;


            case "ADD_RECORD_TO_TABLE":

                try{
                    database.get(index).add_record(words);
                    check_foreign_key();
                }catch (Exception e){
                    System.out.print("<ADD_RECORD_TO_TABLE>   ");
                    System.out.println(e);
                }
                break;


            case "ADD_TABLE_BY_FILE":
                try{
                    add_tables_byfile(words[2], words[1]);
                    check_foreign_key();
                }catch (Exception e){
                    System.out.print("<ADD_TABLE_BY_FILE>   ");
                    System.out.println(e);
                }

                break;



            case "ADD_TABLE":
                try{
                    add_table(words[1], words);
                    check_foreign_key();
                }catch (Exception e){
                    System.out.print("<ADD_TABLE>   ");
                    System.out.println(e);
                }
                break;



            case "ADD_COLUMN":
                try{
                    database.get(index).add_column(words);
                    check_foreign_key();
                }catch (Exception e){
                    System.out.print("<ADD_COLUMN>   ");
                    System.out.println(e);
                }
                break;



            case "DELETE_COLUMN":
                try{
                    database.get(index).delete_column(words[1]);
                }catch (Exception e){
                    System.out.print("<DELETE_COLUMN>   ");
                    System.out.println(e);
                }
                break;



            case "DELETE_N_COLUMNS":
                try{
                    database.get(index).delete_n_column(words[1], words[2]);
                }catch (Exception e){
                    System.out.print("<DELETE_N_COLUMNS>   ");
                    System.out.println(e);
                }
                break;



            case "DELETE_RECORD":
                try{
                    database.get(index).delete_record(Integer.valueOf(words[1]));
                }catch (Exception e){
                    System.out.print("<DELETE_RECORD>   ");
                    System.out.println(e);
                }
                break;



            case "DELETE_N_RECORDS":
                try{
                    database.get(index).delete_n_record(Integer.valueOf(words[1]), Integer.valueOf(words[2]));
                }catch (Exception e){
                    System.out.print("<DELETE_N_RECORDS>   ");
                    System.out.println(e);
                }
                break;



            case "DELETE_TABLE":
                try{
                    delete_table(words[1]);
                }catch (Exception e){
                    System.out.print("<DELETE_TABLE>   ");
                    System.out.println(e);
                }
                break;




            case "LOAD_CODE_FROM_FILE":
                try{
                    file_parser(words[1]);
                }catch (Exception e){
                    System.out.print("<LOAD_CODE_FROM_FILE>   ");
                    System.out.println(e);
                }

                break;



            case "PRINT_TABLE":
                try{
                    database.get(index).print_table();
                }catch (Exception e){
                    System.out.print("<PRINT_TABLE>   ");
                    System.out.println(e);
                }
                break;



            case "PRINT_TABLE_INFO":
                try{
                    database.get(index).print_enum_type();
                }catch (Exception e){
                    System.out.print("<PRINT_TABLE_INFO>   ");
                    System.out.println(e);
                }
                break;



            case "SHOW_DATABASE":
                try{
                    list_table();
                }catch (Exception e){
                    System.out.print("<SHOW_DATABASE>   ");
                    System.out.println(e);
                }
                break;



            case "SEARCH_BY_KEY":
                try{
                    database.get(index).search_record_by_key(words);
                }catch (Exception e){
                    System.out.print("<SEARCH_BY_KEY>   ");
                    System.out.println(e);
                }
                break;



            case "SELECT_TABLE":
                try{
                    select_table(words[1]);
                }catch (Exception e){
                    System.out.print("<SELECT_TABLE>   ");
                    System.out.println(e);
                }
                break;




            case "SAVE":
                try{
                    database.get(index).save(words[1], Boolean.valueOf(words[2]));
                }catch (Exception e){
                    System.out.print("<SAVE>   ");
                    System.out.println(e);
                }
                break;



            case "SAVE_AS":
                try{
                    database.get(index).save_as(words[2], words[1]);
                }catch (Exception e){
                    System.out.print("<SAVE_AS>   ");
                    System.out.println(e);
                }
                break;



            case "UPDATE_CELL":
                try{
                    database.get(index).update_cell(Integer.valueOf(words[1]), Integer.valueOf(words[2]), words[3]);
                }catch (Exception e){
                    System.out.print("<UPDATE_CELL>   ");
                    System.out.println(e);
                }
                break;




            default:
                System.out.println("Invalid Instruction");
        }
    }

    public static void main(String args[]){
        Databases test = new Databases();


        //here i test some basic function i build in this class like:
        //add_tables_byfile(), add_table(), delete_table(), select_table(), command(), check_foreign_key()
        test.add_tables_byfile("/home/kevin/Documents/key_testing.txt", "Test1");//<-----Change the directory
        assert(test.index == 0);
        assert(test.table_name.contains("Test1"));
        assert(test.table_name.size() == 1);
        assert(test.database.size() == 1);
        assert(test.database.get(0).row_number() == 7);
        assert(test.database.get(0).col_number() == 4);
        String[] words0 = {"AUTO_GEN_KEY"};
        test.commands(words0);
        assert(test.database.get(0).row_number() == 7);
        assert(test.database.get(0).col_number() == 5);
        test.database.get(0).print_table();


        String[] words = {"ADD_TABLE","Test2","first_name", "(", "STRING", ")", "last_name", "(", "STRING", ")", "age", "(", "INT", ")"};
        test.add_table(words[1], words);
        test.select_table("Test2");
        assert(test.index == 1);
        String[] words2 = {"ADD_RECORD_TO_TABLE", "kevin", "ho", "22" };
        String[] words3 = {"ADD_RECORD_TO_TABLE", "john", "cena", "55" };
        test.commands(words2);
        test.commands(words3);
        assert(test.table_name.contains("Test2"));
        assert(test.table_name.size() == 2);
        assert(test.database.get(1).row_number() == 3);
        assert(test.database.get(1).col_number() == 3);
        test.database.get(1).print_table();


        //this will test the foreign_key check function
        //this first this Test3 table contain a column <Partner ( FOREIGN_KEY Test1 )>, it has the foreign key form table Test1
        //which was created, first the 3.3 is gone due to the data type check which auto_key is INT type
        //then the 0 and 10 are gone cause Test1 table only has key form 1 to 6 so 0 and 10 are invalid keys
        test.add_tables_byfile("/home/kevin/Documents/kevin_test.txt", "Test3");//<-----Change the directory
        test.select_table("Test3");
        assert(test.database.size() == 3);
        assert(test.index == 2);
        assert(test.database.get(2).col_number() == 6);
        assert(test.database.get(2).row_number() == 7);
        test.database.get(2).print_table();
        test.check_foreign_key();
        System.out.println("!!!!!!!!!!after the type check function!!!!!!!!!!!!!!");
        test.database.get(2).print_table();
        test.list_table();


        //i delete the mother table which is Test1 so two error massage should pop up like for Test3 and Test4 by the check_foreign_key()
        //saying the Test1 table is missing
        System.out.println("Add a table with foreign key without having the parent table existence, an error massage should pop up");
        System.out.println();
        test.delete_table("Test1");
        test.add_tables_byfile("/home/kevin/Documents/kevin_test.txt", "Test4");//<-----Change the directory
        test.select_table("Test4");
        test.check_foreign_key();

        //now i add back the Test1 table but without calling the AutoGenKey
        //this show now give us the error of missing Auto_Gen_Key column
        test.add_tables_byfile("/home/kevin/Documents/key_testing.txt", "Test1");//<-----Change the directory
        test.check_foreign_key();

        test.delete_table("Test1");
        test.delete_table("Test2");
        test.delete_table("Test3");
        test.delete_table("Test4");
        test.list_table();
        assert(test.database.size() == 0);


        Databases test2 = new Databases();

        test2.add_tables_byfile("/home/kevin/Documents/key_testing.txt", "Test1");//<-----Change the directory
        test2.database.get(0).auto_gen_key();
        test2.database.get(0).print_table();
        LinkedList<String> testing = test2.database.get(0).copy_key_column();
        for (String i : testing){
            System.out.println(i);
        }
        assert(testing.get(1).equals("1"));
        assert(testing.get(2).equals("2"));
        assert(testing.get(6).equals("6"));

        test2.add_tables_byfile("/home/kevin/Documents/kevin_test.txt", "Test2");//<-----Change the directory
        test2.check_foreign_key();
        test2.database.get(1).print_table();



        //further testing on the foreign key checking
        Databases test3 = new Databases();
        String[] words_test2 = {"ADD_TABLE","Test1","first_name", "(", "STRING", ")", "last_name", "(", "STRING", ")", "partner", "(", "FOREIGN_KEY", "Test2", ")", "partner2", "(", "FOREIGN_KEY", "Test3", ")"};
        test3.add_table("Test1",words_test2);
        String[] words_test3 = {"ADD_RECORD_TO_TABLE", "kevin", "ho", "1", "2" };
        test3.database.get(0).add_record(words_test3);
        test3.check_foreign_key();
        System.out.println("No Table2 exist");
        test3.list_table();
        test3.database.get(0).print_table();
        //############both partner should be NULL as neither Table2 or Table3 exist

        test3.add_tables_byfile("/home/kevin/Documents/key_testing.txt", "Test2");//<-----Change the directory
        test3.check_foreign_key();
        System.out.println("Table2 exist but it doesnt has AUTO_KEY");
        test3.list_table();
        test3.database.get(0).print_table();
        //#############now Table2 exist but it doesnt have AUTO_KEY so partner should still be NULL

        test3.database.get(1).auto_gen_key();
        test3.database.get(0).add_record(words_test3);
        test3.check_foreign_key();
        test3.database.get(0).print_table();
        //##################now the partner in Table1 should be 1 and partner2 should be NULL as Table3 doesn't exist

        test3.add_tables_byfile("/home/kevin/Documents/key_testing.txt", "Test3");//<-----Change the directory
        test3.database.get(2).auto_gen_key();
        test3.database.get(0).add_record(words_test3);
        test3.check_foreign_key();
        test3.database.get(0).print_table();
        test3.list_table();
        //##########finally i load in the same file as Test3 and add AUTO_KEY to it
        //##########now in Table1, partner should be 1 and partner2 should be 2 and both Test2 and Test3 table exists with AUTO_KEY

    }
}
