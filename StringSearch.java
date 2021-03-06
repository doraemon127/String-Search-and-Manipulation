import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


class StringSearch{
    /*
    The main method should expect 3 command-line arguments:
    $ java StringSearch "<file>" "<query>" "<transform>"
    The overall goal of StringSearch is to take a file of text, search for lines in the file based on some queries, then print out the matching lines after transforming them somehow.
    */
    public static void main(String[] args) throws IOException{

        if (args.length == 0) {
            System.out.println("Enter something smh");
            return;
        }
        String contents = Files.readString(Paths.get(args[0]));
        String[] lines_array = contents.split("\n");
        List<String> lines = new ArrayList<String>();
        lines = Arrays.asList(lines_array);
        
        if (args.length >= 2) { //if query(s) given
            lines = complex_query_processor(args[1], lines);
            //lines = query_processor(args[1], lines); //later edit this to complex_query (including &) method call
        }
        if (args.length == 3) { //if transform(s) given
            lines = complex_transform_processor(args[2], lines); //later edit this to complex_transform (including &) method call
        }
        
        print_lines(lines);
        return;
    }//end of main


    //takes String of &-seperated queries and performs them from left to right
    static List<String> complex_query_processor(String queries, List<String> lines) {
        int ampersand_count = queries.length() - queries.replace("&", "").length();
        for (int i = 0; i <= ampersand_count; i++) {
            if (queries.indexOf("&") != -1) {
                String current_query = queries.substring(0, queries.indexOf("&"));
                //System.out.println(current_query);
                lines = query_processor(current_query, lines);
                queries = queries.substring(queries.indexOf("&") + 1); //remove completed query from queries
            }
            else {
                //System.out.println(queries);
                return query_processor(queries, lines);
            }
        }
        return lines;  
    }


    //takes String of &-seperated transforms and performs them from left to right
    static List<String> complex_transform_processor(String transforms, List<String> lines) {
        int ampersand_count = transforms.length() - transforms.replace("&", "").length();
        for (int i = 0; i <= ampersand_count; i++) {
            if (transforms.indexOf("&") != -1) {
                String current_transform = transforms.substring(0, transforms.indexOf("&"));
                lines = transform_processor(current_transform, lines);
                transforms = transforms.substring(transforms.indexOf("&") + 1); //remove completed transform from transforms
            }
            else {
                return transform_processor(transforms, lines);
            }
        }
        return lines;  
    }


    static List<String> query_processor(String query, List<String> lines) {

        Query q = new Query();

        //can't do if(query.contains("contains") && !(query.contains("not"))) because keyword could also be "not"
        if (query.startsWith("contains")) {
            String keyword = query.substring(query.indexOf("=") + 2, query.length() - 1); //+2 to account for starting ' and -1 to account for ending '
            return q.contains(lines, keyword); 
        }
        if (query.startsWith("length")) {
            int len = Integer.parseInt(query.substring(query.indexOf("=") + 1, query.length()));
            return q.length(lines, len);
        }
        if (query.startsWith("greater")) {
            int len = Integer.parseInt(query.substring(query.indexOf("=") + 1, query.length()));
            return q.greater(lines, len);
        }
        if (query.startsWith("less")) {
            int len = Integer.parseInt(query.substring(query.indexOf("=") + 1, query.length()));
            return q.less(lines, len);
        }
        if (query.startsWith("starts")) {
            String keyword = query.substring(query.indexOf("=") + 2, query.length() - 1); //+2 to account for starting ' and -1 to account for ending '
            return q.starts(lines, keyword); 
        }
        if (query.startsWith("ends")) {
            String keyword = query.substring(query.indexOf("=") + 2, query.length() - 1); //+2 to account for starting ' and -1 to account for ending '
            return q.ends(lines, keyword); 
        }
        if (query.startsWith("not")) { 
            String inner_query = query.substring(query.indexOf("(") + 1, query.indexOf(")"));
            List<String> inner_query_result = query_processor(inner_query, lines);
            //lines.removeAll(inner_query_result); //there can be multiple of the same line in a file, so use removeAll()
            //NOTE: to use removeAll(), in main method need to makes lines = lines.addAll(Arrays.asList(lines_array))
            //return lines;
            List<String> result = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                if (inner_query_result.indexOf(lines.get(i)) == -1) {
                    result.add(lines.get(i));
                }
            }
            return result;
        }

        return lines;
    }//end of query_processor


    static List<String> transform_processor(String transform, List<String> lines) {

        Transform t = new Transform();

        if(transform.startsWith("upper")) {
            return t.upper(lines);
        }
        if(transform.startsWith("lower")) {
            return t.lower(lines);
        }
        if(transform.startsWith("first")) {
            int len = Integer.parseInt(transform.substring(transform.indexOf("=") + 1, transform.length()));
            return t.first(lines, len);
        }
        if(transform.startsWith("last")) {
            int len = Integer.parseInt(transform.substring(transform.indexOf("=") + 1, transform.length()));
            return t.last(lines, len);

        }
        if(transform.startsWith("replace")) { //replace=<string>;<string> 
            String current_word = transform.substring(transform.indexOf("=") + 2, transform.indexOf(";") - 1);
            String new_word = transform.substring(transform.indexOf(";") + 2, transform.length() - 1);
            return t.replace(lines, current_word, new_word);
        }

        return lines;
    }//end of transform_processor


    static void print_lines(List<String> res) {
        for (String line : res) {
            System.out.println(line);
        }
    }//end of print_lines()

}//end of StringSearch class



class Query{

    List<String> contains(List<String> input_lines, String keyword) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < input_lines.size(); i++) {
            if (input_lines.get(i).contains(keyword)) {
                result.add(input_lines.get(i));
            }
        }
        return result;
    }

    List<String> length(List<String> input_lines, int len) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < input_lines.size(); i++) {
            if (input_lines.get(i).length() == len) {
                result.add(input_lines.get(i));
            }
        }
        return result;
    }

    List<String> greater(List<String> input_lines, int len) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < input_lines.size(); i++) {
            if (input_lines.get(i).length() > len) {
                result.add(input_lines.get(i));
            }
        }
        return result;
    }

    List<String> less(List<String> input_lines, int len) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < input_lines.size(); i++) {
            if (input_lines.get(i).length() < len) {
                result.add(input_lines.get(i));
            }
        }
        return result;
    }

    List<String> starts(List<String> input_lines, String keyword) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < input_lines.size(); i++) {
            if (input_lines.get(i).startsWith(keyword)) {
                result.add(input_lines.get(i));
            }
        }
        return result;
    }

    List<String> ends(List<String> input_lines, String keyword) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < input_lines.size(); i++) {
            if (input_lines.get(i).endsWith(keyword)) {
                result.add(input_lines.get(i));
            }
        }
        return result;
    }

}//end of Query class


class Transform{

    List<String> upper(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String new_value = lines.get(i).toUpperCase();
            lines.set(i, new_value);
        }
        return lines;
    }

    List<String> lower(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String new_value = lines.get(i).toLowerCase();
            lines.set(i, new_value);
        }
        return lines;
    }

    List<String> first(List<String> lines, int len) {
        for (int i = 0; i < lines.size(); i++) {
            if(lines.get(i).length() >= len) {
                String new_value = lines.get(i).substring(0, len);
                //System.out.println(new_value);
                lines.set(i, new_value);
            }
        }
        return lines;
    }

    List<String> last(List<String> lines, int len) {
        for (int i = 0; i < lines.size(); i++) {
            if(lines.get(i).length() >= len) {
                String new_value = lines.get(i).substring(lines.get(i).length() - len);
                lines.set(i, new_value);
            }
        }
        return lines;
    }

    List<String> replace(List<String> lines, String current_word, String new_word) {
        //System.out.println(current_word);
        //System.out.println(new_word);
        for (int i = 0; i < lines.size(); i++) {
            lines.set(i, lines.get(i).replaceAll(current_word, new_word));
        }
        return lines;
    }

}//end of Transform class

  