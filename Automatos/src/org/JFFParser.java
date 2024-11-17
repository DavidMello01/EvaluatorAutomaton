package org;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JFFParser {

    public static Automaton parseJFF(String filePath) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new File(filePath));
        Element root = document.getRootElement();
        Element automaton = root.getChild("automaton");

        Set<String> states = new HashSet<>();
        Set<String> finalStates = new HashSet<>();
        String startState = null;

        // Processa todos os estados
        for (Object obj : automaton.getChildren("state")) {
            Element stateElement = (Element) obj;
            String stateId = stateElement.getAttributeValue("id"); // Usar 'id' como identificador do estado

            states.add(stateId);

            // Identifica o estado inicial
            if (stateElement.getChild("initial") != null) {
                startState = stateId;  // Define o estado inicial como o 'id'
            }

            // Adiciona aos estados finais se houver a tag <final>
            if (stateElement.getChild("final") != null) {
                finalStates.add(stateId);  // Adiciona o 'id' aos estados finais
            }
        }

        // Verifica se o estado inicial foi encontrado
        if (startState == null) {
            throw new RuntimeException("Estado inicial não encontrado no arquivo .jff.");
        }

        // Cria o autômato com os estados processados
        Automaton automatonObj = new Automaton(states, startState, finalStates);

        // Processa todas as transições
        for (Object obj : automaton.getChildren("transition")) {
            Element transition = (Element) obj;
            String from = transition.getChildText("from");
            String to = transition.getChildText("to");
            String read = transition.getChildText("read");

            // Adiciona a transição ao autômato
            automatonObj.addTransition(from, to, read);
        }

        // Debug: imprimir informações do autômato
        System.out.println("Estado inicial: " + startState);
        System.out.println("Estados finais: " + finalStates);
        System.out.println("Transições:");
        for (String from : automatonObj.getTransitions().keySet()) {
            for (String read : automatonObj.getTransitions().get(from).keySet()) {
                String to = automatonObj.getTransitions().get(from).get(read);
                System.out.println("De " + from + " para " + to + " com leitura '" + read + "'");
            }
        }

        return automatonObj;
    }
}
