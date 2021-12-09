package domain;

import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private final Grammar grammar;

    public Parser(Grammar grammar) {
        this.grammar = grammar;
    }
//afisare value dupa fiecare add
    public Map<List<String>, List<List<String>>> closureLR(String input) {
        Map<List<String>, List<List<String>>> P = new HashMap<>();
        List<String> lineListT = Arrays.asList(input.split("->"));
        List<String> lineList =
                lineListT.stream().map(String::trim).collect(Collectors.toList());
        //lineList : [S, .A B]
        List<String> key = Arrays.asList(lineList.get(0).strip().split(" \\| "));
        List<List<String>> value = new ArrayList<>();
        List<String> token = List.of(lineList.get(1).split("\\|"));
        for(var str:token){
            List<String> prod = Arrays.asList(str.strip().split(" "));
            value.add(prod);
        }
        //split la lista dupa |
        P.put(key, value);
//        System.out.println(value);
        int size = 0;
        int index = -1;
        String nonT;
//        pana aici o sa fie {[S]:[[.A, B]}
        while (size < P.size()) {
            size = P.size();
            Map<List<String>, List<List<String>>> filteredP = clone(P);
            for (Map.Entry element : filteredP.entrySet()) {
                value = (List<List<String>>) element.getValue();
                // value o sa fie o lista de liste de strings [[.A, B]]
                for (List<String> s : value) {
                    index = -1;
                    for(String character: s){
                        if(character.charAt(0) == '.'){
                            index = s.indexOf(character);
                            break;
                        }
                    }
                    //we have the index of the dotted element
                    if (index != -1) {
                        s.set(index, s.get(index).substring(1));
                        nonT = s.get(index);
                        // nonT - dotted element
                        Map<List<String>, List<List<String>>> filteredB = clone(grammar.filterP(nonT));
                        for (Map.Entry elementB : filteredB.entrySet()) {
                            List<String> keyB = (List<String>) elementB.getKey();
                            List<List<String>> valueB = (List<List<String>>) elementB.getValue();
                            if (!P.containsKey(keyB)) {
                                for(var q: valueB)
                                    q.set(0, "."+q.get(0));
                                P.put(keyB, valueB);
//                                System.out.println(valueB);
                            }
                        }
                    }
                }
            }
        }
        return P;
    }
    public static Map<List<String>, List<List<String>>> clone(Map<List<String>, List<List<String>>> original)
    {
        Map<List<String>, List<List<String>>> copy = new HashMap<>();
        for (Map.Entry elementB : original.entrySet()) {
            List<String> key = new ArrayList<>();
            List<String> keyOriginal = (List<String>) elementB.getKey();
            key.add(keyOriginal.get(0));
            List<List<String>> values = new ArrayList<>();
            for(var vls: (List<List<String>>)elementB.getValue()){
                List<String> value = new ArrayList<>();
                for(var vl: vls){
                    value.add(vl);
                }
                values.add(value);
            }
            copy.put(key, values);
        }
        return copy;
    }

    public Map<List<String>, List<List<String>>> goTo(Map<List<String>, List<List<String>>> productions, String symbol) {
        Map<List<String>, List<List<String>>> nestedMap = new HashMap<>();
        Map<List<String>, List<List<String>>> productionscopy = clone(productions);
        for (Map.Entry element : productionscopy.entrySet()) {
            List<List<String>> values = (List<List<String>>) element.getValue();
            List<String> key = (List<String>) element.getKey();
            System.out.println(values);
            boolean symbolFound = false;
            for(var value: values){
                for(int i = 0; i < value.size(); i++) {
                    if (value.get(i).equals("."+symbol)) {
                        System.out.println(values.get(i));
                        value.set(i, value.get(i).substring(1));
                        symbolFound = true;
                        if (i == value.size() - 1) {
                            value.set(i, value.get(i) + ".");
                        } else {
                            value.set(i + 1, "." + value.get(i + 1));
                        }
                        break;
                    }
                }
            }
            if(symbolFound) {
                System.out.println(values);
                String closureString = key.get(0) + " ->";
                boolean or = false;
                for (var value : values) {
                    if (or)
                        closureString += " |";
                    or = false;
                    for (var val : value) {
                        closureString += " " + val;
                        or = true;
                    }
                }
                Map<List<String>, List<List<String>>> closure = clone(closureLR(closureString));
                nestedMap.putAll(closure);
            }
        }
        return nestedMap;
    }
}
