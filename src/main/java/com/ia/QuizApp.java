package com.ia;

import com.ia.model.Answer;
import com.ia.model.Question;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class QuizApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, String> subjects = new HashMap<>();
    static {
        subjects.put("1", "AA");
        subjects.put("2", "IIA");
        subjects.put("3", "NC");
        subjects.put("4", "PC");
        subjects.put("5", "RPA");
    }

    private static TreeMap <Integer, String> topics = new TreeMap <>();
    private static List<Question> questions = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        String subjectChoice = chooseSubject();
        while(!subjectChoice.equals("0")) {
            String topic = chooseTopic(subjectChoice);
            questions = loadQuestions(subjectChoice, topic);
            System.out.println(askQuestion());
            clearScreen();
            subjectChoice = chooseSubject();
        }

    }

    private static String chooseSubject() throws IOException {
        System.out.println("Elige una asignatura:");
        subjects.forEach((key, value) -> System.out.println(key + " - " + value));
        return scanner.nextLine();
    }

    private static String chooseTopic(String subjectChoice) throws IOException {
        System.out.println("Elige un tema:");
        loadTopics(subjectChoice);
        topics.forEach((key, value) -> System.out.println(key + " - " + value));
        return scanner.nextLine();
    }

    private static String askQuestion() throws IOException {
        float score = 0;
        float scoreTotal = 0;
        for (Question question : questions) {
            clearScreen();
            System.out.println("Tu puntuación es: " + score/scoreTotal * 100 + "%");
            if(question.isMultipleChoice()) {
                System.out.println("PREGUNTA MULTIRESPUESTA");
            }
            System.out.println(question.getQuestion());
            int i = 1;
            for (Answer answer : question.getAnswers()) {
                System.out.println(i + " - " + answer.getAnswer());
                i++;
            }
            String response = scanner.nextLine();
            if(response.equals("0")) {
                return "Tu puntación total es: " + score/scoreTotal * 100 + "%";
            }
            score += validateResponse(response, question.getAnswers());
            scoreTotal += 1;
        }
        return "Tu puntación total es: " + score/scoreTotal * 100 + "%";
    }

    private static void clearScreen() {
        for(int i = 0; i < 25; i++) {
            System.out.println();
        }
    }

    private static float validateResponse(String response, List<Answer> answers) {
        List<Integer> answerIndexes = new ArrayList<>();
        int correctCount = 0;
        int totalValidas = (int) answers.stream()
                .filter(Answer::isCorrect) // Filtra solo las respuestas correctas
                .count();
        for (int i = 0; i < response.length(); i++) {
            int answerIndex = Character.getNumericValue(response.charAt(i)) - 1;
            // Verifica si el índice está dentro del rango de la lista de respuestas y si la respuesta es correcta
            if (answerIndex >= 0 && answerIndex < answers.size() && answers.get(answerIndex).isCorrect()) {
                System.out.println("Respuesta correcta");
                scanner.nextLine();
                correctCount++;
            } else {
                System.out.println("Respuesta incorrecta");
                System.out.println("Falso: " + answers.get(answerIndex).getAnswer());
                scanner.nextLine();
            }
            answerIndexes.add(answerIndex);
        }
        Collections.sort(answerIndexes, Collections.reverseOrder());
        for (int index : answerIndexes) {
            if (index >= 0 && index < answers.size()) {
                answers.remove(index);
            }
        }

        for (Answer answer : answers) {
            if (answer.isCorrect()) {
                System.out.println("Respuesta incorrecta");
                System.out.println("Verdadero: " + answer.getAnswer());
                scanner.nextLine();
            }
        }
        return correctCount/totalValidas;
    }

    private static void loadTopics(String folder) {
        topics = new TreeMap<>();
        try {
            // Obtiene la URL del recurso del directorio especificado
            URL resourceFolder = QuizApp.class.getClassLoader().getResource(subjects.get(folder));
            if (resourceFolder == null) {
                throw new IOException("No se puede encontrar el directorio de recursos: " + folder);
            }

            // Obtiene todos los archivos del directorio
            File dir = new File(resourceFolder.toURI());
            File[] files = dir.listFiles();

            if (files != null) {
                int i = 1;

                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        topics.put(i, "Tema" + Integer.toString(i));
                        i++;
                    }
                }
                topics.put(i, "Todos los temas");
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static List<Question> loadQuestions(String subjectChoice, String topic) {
        questions = new ArrayList<>();
        Question currentQuestion = null;

        try {
            URL resourceFolder = QuizApp.class.getClassLoader().getResource(subjects.get(subjectChoice));
            if (resourceFolder == null) {
                throw new IOException("No se puede encontrar el directorio de recursos: " + subjectChoice);
            }

            File dir = new File(resourceFolder.toURI());
            File[] files = dir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (topics.get(Integer.parseInt(topic)).equals("Todos los temas") || file.getName().equals("Tema" + topic + ".txt")) {
                        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

                        for (String line : lines) {
                            if (line.startsWith("|") || line.startsWith("\\")) {
                                if (currentQuestion != null) {
                                    if(!containsVariousAnswers(currentQuestion.getAnswers())) {
                                        Collections.shuffle(currentQuestion.getAnswers());
                                    }
                                    questions.add(currentQuestion); // Agrega la pregunta anterior a la lista
                                }
                                currentQuestion = new Question();
                                currentQuestion.setQuestion(line.substring(1)); // Elimina el primer caracter
                                currentQuestion.setMultipleChoice(line.startsWith("\\"));
                                currentQuestion.setAnswers(new ArrayList<Answer>());
                            } else if (line.startsWith("+") || line.startsWith("-")) {
                                if (currentQuestion != null) {
                                    Answer answer = new Answer();
                                    answer.setAnswer(line.substring(1)); // Elimina el primer caracter
                                    answer.setCorrect(line.startsWith("+"));
                                    currentQuestion.getAnswers().add(answer);
                                }
                            }
                        }

                        // Agrega la última pregunta si existe
                        if (currentQuestion != null) {
                            questions.add(currentQuestion);
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        Collections.shuffle(questions);
        return questions;
    }

    private static boolean containsVariousAnswers(List<Answer> answers) {
        for (Answer answer : answers) {
            if(answer.getAnswer().contains(" son correctas") && !answer.getAnswer().contains("Todas")) {
                return true;
            }
        }
        return false;
    }

}
