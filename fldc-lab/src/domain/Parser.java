package domain;

import java.util.*;
import java.util.stream.Collectors;

public class Parser {
    private final Grammar grammar;

    public Parser(Grammar grammar) {
        this.grammar = grammar;
    }

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
        int size = 0;
        int index = -1;
        String nonT;
//        pana aici o sa fie {[S]:[[.A, B]}
        while (size < P.size()) {
            size = P.size();
            Map<List<String>, List<List<String>>> filteredP = new HashMap<>(P);
            for (Map.Entry element : filteredP.entrySet()) {
                value = (List<List<String>>) element.getValue();
                // value o sa fie o lista de liste de strings [[.A, B]]
                for (List<String> s : value) {
                    index = -1;
                    System.out.println(s);
                    for(String character: s){
                        if(character.charAt(0) == '.'){
                            index = s.indexOf(character);
                            break;
                        }
                    }
                    //gasim indexul punctului
                    if (index != -1) {
                        s.set(index, s.get(index).substring(1));
                        nonT = s.get(index);
                        System.out.println(nonT);
                        // o sa fie primul element acuma nonT
                        Map<List<String>, List<List<String>>> filteredB = grammar.filterP(nonT);
                        System.out.println(filteredB);
//                        //System.out.println(filteredB);
//                        for (Map.Entry elementB : filteredB.entrySet()) {
//                            List<String> keyB = (List<String>) elementB.getKey();
//                            List<String> valueB = (List<String>) elementB.getValue();
//                            if (!P.containsKey(keyB)) {
//                                P.put(keyB, valueB.stream().map(x -> "." + x).collect(Collectors.toList()));
//                            }
//                        }
                    }
                }
            }
        }
        return P;
    }
}
