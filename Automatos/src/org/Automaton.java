package org;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Automaton {
    private Map<String, String> states = new HashMap<>();  // Armazena os estados por ID
    private Set<String> finalStates = new HashSet<>();
    private String initialState;
    private Map<String, Map<String, String>> transitions = new HashMap<>();  // Transições do autômato

    // Construtor que aceita os estados, o estado inicial e os estados finais
    public Automaton(Set<String> states, String initialState, Set<String> finalStates) {
        this.states = new HashMap<>();
        for (String state : states) {
            this.states.put(state, state);
        }
        this.initialState = initialState;
        this.finalStates = finalStates;
        this.transitions = new HashMap<>();
    }

    // Adiciona uma transição ao autômato
    public void addTransition(String from, String to, String read) {
        if (!transitions.containsKey(from)) {
            transitions.put(from, new HashMap<>());
        }
        transitions.get(from).put(read, to);
    }

    // Retorna todas as transições a partir de um estado com determinado símbolo
    public Map<String, String> getTransitionsFrom(String from) {
        return transitions.get(from);
    }

    // Método para obter o estado inicial como String
    public String getInitialStateId() {
        return initialState;
    }

    // Método que retorna os próximos estados a partir de um estado e símbolo
    public Set<Transition> getNextStates(String state, char symbol) {
        Set<Transition> reachableStates = new HashSet<>();
        if (transitions.containsKey(state)) {
            Map<String, String> transitionMap = transitions.get(state);
            String symbolString = String.valueOf(symbol);
            if (transitionMap.containsKey(symbolString)) {
                String nextState = transitionMap.get(symbolString);
                reachableStates.add(new Transition(state, nextState));
            }
        }
        return reachableStates;
    }
    
    public boolean isDeterministic() {
        for (Map.Entry<String, Map<String, String>> stateEntry : transitions.entrySet()) {
            Map<String, String> transitionMap = stateEntry.getValue();
            
            // Verifica se existem transições duplicadas para o mesmo símbolo
            Set<String> symbols = new HashSet<>();
            for (String symbol : transitionMap.keySet()) {
                if (symbols.contains(symbol)) {
                    // Se já existe uma transição para o símbolo atual, o autômato é não determinístico
                    return false;
                }
                symbols.add(symbol);
            }
        }
        return true; // Se não encontrou conflitos, o autômato é determinístico
    }


    // Verifica se o estado fornecido é final
    public boolean isFinalState(String state) {
        return finalStates.contains(state);
    }

    public Map<String, Map<String, String>> getTransitions() {
        return transitions;
    }

    public String getInitialState() {
        return initialState;
    }

    public Set<String> getFinalStates() {
        return finalStates;
    }
}