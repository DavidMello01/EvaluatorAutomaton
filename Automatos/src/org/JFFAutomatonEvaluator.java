package org;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import org.jdom.input.SAXBuilder;
import org.jdom.*;
import java.util.Set;

public class JFFAutomatonEvaluator {

    private static Set<Integer> currentStates;  // Conjunto de estados possíveis em um dado momento
    private static Set<Integer> finalStates;    // Conjunto de estados finais
    private static Map<Integer, Map<Character, Set<Integer>>> transitions; // Transições do autômato
    private static int correctCount = 0;        // Contador de acertos
    private static int incorrectCount = 0;      // Contador de erros

    public static void main(String[] args) {
        // Caminho do arquivo de entrada com as sequências
        String filePath = "C:\\Users\\pesso\\Downloads\\metro-main\\Automatos\\text_sequences.txt"; // Atualize o caminho conforme necessário
        String jffFilePath = "C:\\Users\\pesso\\Downloads\\metro-main\\Automatos\\meuAutomato.jff"; // Caminho para o arquivo JFF
        
        // Leitura do arquivo e avaliação das sequências
        List<String> inputs = readTestStringsFromFile(filePath);
        
        // Parse do arquivo JFF
        parseJFFFile(jffFilePath);
        
        // Avaliação das entradas
        for (String input : inputs) {
            boolean accepted = evaluateInput(input); // Substitua por seu método de avaliação
            if (accepted) {
                correctCount++;
                System.out.println("Entrada \"" + input + "\" aceita.");
            } else {
                incorrectCount++;
                System.out.println("Entrada \"" + input + "\" rejeitada.");
            }
        }

        // Exibindo o total de acertos e erros
        System.out.println("\nTotal de acertos: " + correctCount);
        System.out.println("Total de erros: " + incorrectCount);
    }

    // Método para ler as entradas de um arquivo
    public static List<String> readTestStringsFromFile(String filePath) {
        List<String> inputs = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                inputs.add(line); // Adiciona cada linha como uma entrada
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return inputs;
    }

    // Função para fazer o parse do arquivo JFF e armazenar estados, transições e estados finais
    private static void parseJFFFile(String filePath) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new File(filePath));
            Element root = document.getRootElement();
            Element automaton = root.getChild("automaton");

            currentStates = new HashSet<>();
            finalStates = new HashSet<>();
            transitions = new HashMap<>();

            // Parse de estados
            List<Element> states = automaton.getChildren("state");
            for (Element state : states) {
                int stateId = Integer.parseInt(state.getAttributeValue("id"));
                if (state.getChild("initial") != null) {
                    // Se o estado for inicial, inicializa o conjunto de estados atuais
                    currentStates.add(stateId);
                }
                if (state.getChild("final") != null) {
                    finalStates.add(stateId);  // Marca como estado final
                }
            }

            // Parse de transições
            List<Element> transitionsList = automaton.getChildren("transition");
            for (Element transition : transitionsList) {
                int fromState = Integer.parseInt(transition.getChildText("from"));
                int toState = Integer.parseInt(transition.getChildText("to"));
                char readChar = transition.getChildText("read").charAt(0);

                // Adiciona transições ao mapa
                transitions.putIfAbsent(fromState, new HashMap<>());
                transitions.get(fromState).putIfAbsent(readChar, new HashSet<>());
                transitions.get(fromState).get(readChar).add(toState);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Função para avaliar se a sequência é aceita ou rejeitada
    private static boolean evaluateInput(String input) {
        Set<Integer> nextStates = new HashSet<>(currentStates);

        // Para cada símbolo de entrada, atualizamos os estados possíveis
        for (char symbol : input.toCharArray()) {
            Set<Integer> newStates = new HashSet<>();
            for (int state : nextStates) {
                if (transitions.containsKey(state) && transitions.get(state).containsKey(symbol)) {
                    newStates.addAll(transitions.get(state).get(symbol)); // Agrega os próximos estados possíveis
                }
            }
            nextStates = newStates; // Atualiza os estados possíveis

            if (nextStates.isEmpty()) {
                return false; // Se não há estados possíveis, a entrada é rejeitada
            }
        }

        // Verifica se algum estado final foi alcançado
        for (int state : nextStates) {
            if (finalStates.contains(state)) {
                return true;  // Aceita se algum estado final foi alcançado
            }
        }

        return false;  // Rejeita se nenhum estado final foi alcançado
    }
}
